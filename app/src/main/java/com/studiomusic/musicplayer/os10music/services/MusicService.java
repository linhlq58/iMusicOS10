package com.studiomusic.musicplayer.os10music.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.activities.PlayingActivity;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.SongObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Linh Lee on 11/8/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    public static MediaPlayer player;
    private static int currentPos;
    private static ArrayList<SongObject> songsList;
    private static int songPos;
    private final IBinder musicBind = new MusicBinder();
    private static Notification.Builder builder;
    private static Notification noti;
    private RemoteViews contentView;
    private static boolean shuffle = false;
    private static boolean repeat = false;
    private static Random rand;
    private MyApplication app;
    private DatabaseHelper db;
    private static Gson gson;
    private static SharedPreferences sharedPreferences;
    private BroadcastReceiver receiver, receiverUpdate;
    private IntentFilter filter, filterUpdate;

    private static int NOTIFY_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (MyApplication) getApplication();
        db = app.getDatabase();
        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        songPos = 0;
        rand = new Random();
        player = new MediaPlayer();
        initMusicPlayer();
        songsList = db.getAllSongs();

        filter = new IntentFilter("noti_control");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().getString("command").equals("play_pause")) {
                    if (isPlaying()) {
                        contentView.setImageViewResource(R.id.play_pause_btn, R.drawable.img_pause);
                        startForeground(NOTIFY_ID, noti);
                    } else {
                        contentView.setImageViewResource(R.id.play_pause_btn, R.drawable.img_play);
                        startForeground(NOTIFY_ID, noti);
                    }
                } else if (intent.getExtras().getString("command").equals("close")) {
                    onPause();
                    stopForeground(true);
                }
            }
        };
        registerReceiver(receiver, filter);

        filterUpdate = new IntentFilter("update_noti");
        receiverUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isPlaying()) {
                    contentView.setImageViewResource(R.id.play_pause_btn, R.drawable.img_pause);
                    startForeground(NOTIFY_ID, noti);
                } else {
                    contentView.setImageViewResource(R.id.play_pause_btn, R.drawable.img_play);
                    startForeground(NOTIFY_ID, noti);
                }
            }
        };
        registerReceiver(receiverUpdate, filterUpdate);
    }

    public void initMusicPlayer(){
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentPos = 0;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            musicPath = extras.getString("music path");
            Log.d("path", musicPath);

            try {
                player.setDataSource(musicPath);
                player.setLooping(true); // Set looping
                player.setVolume(100, 100);
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 1;
    }*/

    public static void playSong() {
        //play
        player.reset();
        //get song
        SongObject playSong = songsList.get(songPos);
        //set the data source
        try{
            player.setDataSource(playSong.getFullpath());
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public static void playPrev(){
        songPos--;
        if(songPos < 0) songPos = songsList.size() - 1;
        sharedPreferences.edit().putString("current song", gson.toJson(getCurrentSong())).apply();
        playSong();
    }

    public static void playNext(){
        if(shuffle){
            int newSong = songPos;
            while(newSong == songPos){
                newSong = rand.nextInt(songsList.size());
            }
            songPos = newSong;
        }
        else{
            songPos++;
            if(songPos >= songsList.size()) songPos = 0;
        }
        sharedPreferences.edit().putString("current song", gson.toJson(getCurrentSong())).apply();
        playSong();
    }

    //set the song
    public static void setSong(int songId){
        for (int i = 0; i < songsList.size(); i++) {
            if (songsList.get(i).getId() == songId) {
                songPos = i;
            }
        }
    }

    public static void setRepeat(boolean rp) {
        repeat = rp;
    }

    public static void setShuffle(boolean sf) {
        shuffle = sf;
    }

    public static SongObject getCurrentSong() {
        SongObject song = songsList.get(songPos);
        return song;
    }

    public static void onResume() {
        if (currentPos != 0) {
            player.seekTo(currentPos);
        }
        player.start();
    }

    public static void onPause() {
        player.pause();
        currentPos = player.getCurrentPosition();
    }

    public static boolean isPlaying() {
        if (player.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    public static int getCurPos() {
        return player.getCurrentPosition();
    }

    public static int getSongPos() {
        return songPos;
    }

    public static boolean getRepeat() {
        return repeat;
    }

    public static boolean getShuffle() {
        return shuffle;
    }

    public static void setSongProgress(int progress) {
        player.seekTo(progress);
    }

    @Override
    public void onDestroy() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (receiverUpdate != null) {
            unregisterReceiver(receiverUpdate);
        }
        builder.setOngoing(false);
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        createNotification();
        startForeground(NOTIFY_ID, noti);

        Intent i = new Intent("set play button");
        sendBroadcast(i);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (repeat) {
            if(player.getCurrentPosition() > 0){
                mediaPlayer.reset();
                playSong();
            }
        } else {
            if(player.getCurrentPosition() > 0){
                mediaPlayer.reset();
                playNext();
            }
        }

        Intent i = new Intent("song completed");
        sendBroadcast(i);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.d("MUSIC PLAYER", "Playback Error");
        mediaPlayer.reset();
        return false;
    }

    private void createNotification() {
        Intent notiIntent = new Intent(this, PlayingActivity.class);
        notiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent closeButton = new Intent("Song_Control");
        Bundle closeBundle = new Bundle();
        closeBundle.putInt("command", 1);
        closeButton.putExtras(closeBundle);
        PendingIntent pendingCloseIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, closeButton, 0);

        Intent playPauseButton = new Intent("Song_Control");
        Bundle playPauseBundle = new Bundle();
        playPauseBundle.putInt("command", 2);
        playPauseButton.putExtras(playPauseBundle);
        PendingIntent pendingPlayPauseIntent = PendingIntent.getBroadcast(getApplicationContext(), 2, playPauseButton, 0);

        Intent forwardButton = new Intent("Song_Control");
        Bundle forwardeBundle = new Bundle();
        forwardeBundle.putInt("command", 3);
        forwardButton.putExtras(forwardeBundle);
        PendingIntent pendingForwardIntent = PendingIntent.getBroadcast(getApplicationContext(), 3, forwardButton, 0);

        contentView = new RemoteViews(getPackageName(), R.layout.notification_layout_small);
        contentView.setTextViewText(R.id.song_name, getCurrentSong().getName());
        contentView.setTextViewText(R.id.song_artist, getCurrentSong().getArtist());
        contentView.setImageViewResource(R.id.play_pause_btn, R.drawable.img_pause);
        contentView.setImageViewResource(R.id.close_btn, R.drawable.img_close_noti);
        contentView.setImageViewResource(R.id.next_btn, R.drawable.img_forward);
        contentView.setOnClickPendingIntent(R.id.close_btn, pendingCloseIntent);
        contentView.setOnClickPendingIntent(R.id.play_pause_btn, pendingPlayPauseIntent);
        contentView.setOnClickPendingIntent(R.id.next_btn, pendingForwardIntent);

        builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.mipmap.ic_noti)
                .setTicker(getCurrentSong().getName())
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(getCurrentSong().getName()).setContent(contentView);
        noti = builder.build();
    }
}
