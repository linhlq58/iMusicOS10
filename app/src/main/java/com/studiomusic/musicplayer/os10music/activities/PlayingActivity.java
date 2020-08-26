package com.studiomusic.musicplayer.os10music.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.Gson;
import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.services.MusicService;
import com.studiomusic.musicplayer.os10music.utils.Constant;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class PlayingActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static boolean active = false;
    private ImageView mBackButton;
    private ImageView mListButton;
    private FrameLayout mContentLayout;
    private RoundedImageView mAlbumImage;
    private ImageView mPlayPauseImg;
    private ImageView mBackwardImg;
    private ImageView mForwardImg;
    private TextView mCurPosText;
    private TextView mLengthText;
    private SeekBar progressSeekbar;
    private TextView mArtist;
    private TextView mSongName;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private ImageView repeatImg;
    private ImageView shuffleImg;
    private SongObject mSongObject;
    private int contentWidth;
    private int contentHeight;
    private Handler mHandler;
    private SharedPreferences sharedPreferences;
    private MyApplication app;
    private DatabaseHelper db;
    private Gson gson;
    private BroadcastReceiver receiver, receiverCompleted, receiverPlay, receiverNoti, receiverServDestroyed;
    private IntentFilter filter, filterCompleted, filterPlay, filterNoti, filterServDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        app = (MyApplication) getApplication();
        db = app.getDatabase();
        gson = new Gson();

        mBackButton = (ImageView) findViewById(R.id.back_button);
        mListButton = (ImageView) findViewById(R.id.list_button);
        mContentLayout = (FrameLayout) findViewById(R.id.content);
        mAlbumImage = (RoundedImageView) findViewById(R.id.album_image);
        mPlayPauseImg = (ImageView) findViewById(R.id.play_pause_img);
        mBackwardImg = (ImageView) findViewById(R.id.backward_img);
        mForwardImg = (ImageView) findViewById(R.id.forward_img);
        mCurPosText = (TextView) findViewById(R.id.cur_position);
        mLengthText = (TextView) findViewById(R.id.length);
        progressSeekbar = (SeekBar) findViewById(R.id.song_progress);
        mArtist = (TextView) findViewById(R.id.artist);
        mSongName = (TextView) findViewById(R.id.name);
        volumeSeekbar = (SeekBar) findViewById(R.id.volume_seekbar);
        repeatImg = (ImageView) findViewById(R.id.repeat_img);
        shuffleImg = (ImageView) findViewById(R.id.shuffle_img);

        mSongObject = gson.fromJson(sharedPreferences.getString("current song", gson.toJson(db.getAllSongs().get(0))), SongObject.class);

        if (MusicService.isPlaying()) {
            mPlayPauseImg.setImageResource(R.drawable.img_pause);
        } else {
            mPlayPauseImg.setImageResource(R.drawable.img_play);
        }
        if (MusicService.getRepeat()) {
            repeatImg.setImageResource(R.mipmap.ic_repeat);
        } else {
            repeatImg.setImageResource(R.mipmap.ic_repeat_grey);
        }
        if (MusicService.getShuffle()) {
            shuffleImg.setImageResource(R.mipmap.ic_shuffle);
        } else {
            shuffleImg.setImageResource(R.mipmap.ic_shuffle_grey);
        }

        //Setup progress seekbar
        progressSeekbar.setMax(mSongObject.getDuration());
        progressSeekbar.setOnSeekBarChangeListener(this);

        //Setup volume seekbar
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeSeekbar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekbar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeSeekbar.setOnSeekBarChangeListener(this);

        initTextView();
        if (MusicService.isPlaying()) {
            updateCurPosText();
        } else {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
        }

        filter = new IntentFilter("change seek bar");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("test 2", "okkk");
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    int volumeLevel = extras.getInt("volume level");
                    volumeSeekbar.setProgress(volumeLevel);
                }
            }
        };
        registerReceiver(receiver, filter);

        filterCompleted = new IntentFilter("song completed");
        receiverCompleted = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mSongObject = gson.fromJson(sharedPreferences.getString("current song", ""), SongObject.class);
                progressSeekbar.setMax(mSongObject.getDuration());
                initTextView();
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                updateCurPosText();
            }
        };
        registerReceiver(receiverCompleted, filterCompleted);

        filterPlay = new IntentFilter("set play button");
        receiverPlay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPlayPauseImg.setImageResource(R.drawable.img_pause);
                mSongObject = gson.fromJson(sharedPreferences.getString("current song", ""), SongObject.class);
                initTextView();
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                updateCurPosText();
            }
        };
        registerReceiver(receiverPlay, filterPlay);

        filterNoti = new IntentFilter("noti_control");
        receiverNoti = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MusicService.isPlaying()) {
                    mPlayPauseImg.setImageResource(R.drawable.img_pause);
                    updateCurPosText();
                } else {
                    mPlayPauseImg.setImageResource(R.drawable.img_play);
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                }
            }
        };
        registerReceiver(receiverNoti, filterNoti);

        filterServDestroyed = new IntentFilter("service_destroyed");
        receiverServDestroyed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPlayPauseImg.setImageResource(R.drawable.img_play);
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        };
        registerReceiver(receiverServDestroyed, filterServDestroyed);

        mBackButton.setOnClickListener(this);
        mListButton.setOnClickListener(this);
        mPlayPauseImg.setOnClickListener(this);
        mBackwardImg.setOnClickListener(this);
        mForwardImg.setOnClickListener(this);
        repeatImg.setOnClickListener(this);
        shuffleImg.setOnClickListener(this);
        /*progressSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        int curPos = progressSeekbar.getProgress();
                        MusicService.setSongProgress(curPos);
                        break;
                }

                return false;
            }
        });*/

        Constant.increaseHitArea(mBackButton);
        Constant.increaseHitArea(mListButton);
        Constant.increaseHitArea(mPlayPauseImg);
        Constant.increaseHitArea(mBackwardImg);
        Constant.increaseHitArea(mForwardImg);
        Constant.increaseHitArea(repeatImg);
        Constant.increaseHitArea(shuffleImg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Constant.isMyServiceRunning(this, MusicService.class)) {
            Intent playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
        }
        active = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mAlbumImage.getLayoutParams().width = mContentLayout.getWidth() - Constant.convertDpIntoPixels(20, this);
        mAlbumImage.getLayoutParams().height = mContentLayout.getHeight() - Constant.convertDpIntoPixels(20, this);
        mAlbumImage.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (receiverCompleted != null) {
            unregisterReceiver(receiverCompleted);
        }
        if (receiverPlay != null) {
            unregisterReceiver(receiverPlay);
        }
        if (receiverNoti != null) {
            unregisterReceiver(receiverNoti);
        }
        if (receiverServDestroyed != null) {
            unregisterReceiver(receiverServDestroyed);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                finish();
                overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
                break;
            case R.id.play_pause_img:
                if (MusicService.isPlaying()) {
                    mPlayPauseImg.setImageResource(R.drawable.img_play);
                    MusicService.onPause();
                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                    }
                } else {
                    mPlayPauseImg.setImageResource(R.drawable.img_pause);
                    MusicService.onResume();
                    updateCurPosText();
                }
                Intent i = new Intent("update_noti");
                sendBroadcast(i);
                break;
            case R.id.backward_img:
                MusicService.playPrev();
                mSongObject = gson.fromJson(sharedPreferences.getString("current song", ""), SongObject.class);
                progressSeekbar.setMax(mSongObject.getDuration());
                initTextView();
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                updateCurPosText();
                break;
            case R.id.forward_img:
                MusicService.playNext();
                mSongObject = gson.fromJson(sharedPreferences.getString("current song", ""), SongObject.class);
                progressSeekbar.setMax(mSongObject.getDuration());
                initTextView();
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
                updateCurPosText();
                break;
            case R.id.repeat_img:
                if (MusicService.getRepeat()) {
                    repeatImg.setImageResource(R.mipmap.ic_repeat_grey);
                    MusicService.setRepeat(false);
                    Toast.makeText(PlayingActivity.this, "Repeat is OFF", Toast.LENGTH_SHORT).show();
                } else {
                    repeatImg.setImageResource(R.mipmap.ic_repeat);
                    MusicService.setRepeat(true);
                    Toast.makeText(PlayingActivity.this, "Repeat is ON", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shuffle_img:
                if (MusicService.getShuffle()) {
                    shuffleImg.setImageResource(R.mipmap.ic_shuffle_grey);
                    MusicService.setShuffle(false);
                    Toast.makeText(PlayingActivity.this, "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                } else {
                    shuffleImg.setImageResource(R.mipmap.ic_shuffle);
                    MusicService.setShuffle(true);
                    Toast.makeText(PlayingActivity.this, "Shuffle is ON", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*private void updateCurPosText() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCurPosText.post(new Runnable() {
                            @Override
                            public void run() {
                                int curPos = MusicService.getCurPos();
                                int minute = (curPos / 1000) / 60;
                                int second = (curPos / 1000) % 60;
                                mCurPosText.setText(Constant.formatTime(minute) + ":" + Constant.formatTime(second));
                                progressSeekbar.setProgress(curPos);
                            }
                        });
                    }
                });
            }
        }, 0, 1000);
    }*/

    private void updateCurPosText() {
        mHandler = new Handler();
        PlayingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MusicService.player != null) {
                    int curPos = MusicService.getCurPos();
                    int minute = (curPos / 1000) / 60;
                    int second = (curPos / 1000) % 60;
                    mCurPosText.setText(Constant.formatTime(minute) + ":" + Constant.formatTime(second));
                    progressSeekbar.setProgress(curPos);
                }
                mHandler.postDelayed(this, 500);
            }
        });
    }

    private void initTextView() {
        int minute = (mSongObject.getDuration() / 1000) / 60;
        int second = (mSongObject.getDuration() / 1000) % 60;

        int currentPos = MusicService.getCurPos();
        int minuteCur = (currentPos / 1000) / 60;
        int secondCur = (currentPos / 1000) % 60;

        mCurPosText.setText(Constant.formatTime(minuteCur) + ":" + Constant.formatTime(secondCur));
        mLengthText.setText(Constant.formatTime(minute) + ":" + Constant.formatTime(second));
        mArtist.setText(mSongObject.getArtist());
        mSongName.setText(mSongObject.getName());
        progressSeekbar.setProgress(currentPos);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()) {
            case R.id.volume_seekbar:
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
                break;
            case R.id.song_progress:
                if (MusicService.player != null && b) {
                    MusicService.setSongProgress(progress);
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
