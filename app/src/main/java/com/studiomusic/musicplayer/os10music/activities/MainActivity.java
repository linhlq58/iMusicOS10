package com.studiomusic.musicplayer.os10music.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beolla.ZergitasSDK;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.adapters.MyPagerAdapter;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.fragments.AlbumsListFragment;
import com.studiomusic.musicplayer.os10music.fragments.ArtistsListFragment;
import com.studiomusic.musicplayer.os10music.fragments.FavoritesAddFragment;
import com.studiomusic.musicplayer.os10music.fragments.AlbumsFragment;
import com.studiomusic.musicplayer.os10music.fragments.ArtistsFragment;
import com.studiomusic.musicplayer.os10music.fragments.FavoritesFragment;
import com.studiomusic.musicplayer.os10music.fragments.GenresFragment;
import com.studiomusic.musicplayer.os10music.fragments.FavoritesShowFragment;
import com.studiomusic.musicplayer.os10music.fragments.SongsFragment;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.services.MusicService;
import com.studiomusic.musicplayer.os10music.utils.Constant;
import com.studiomusic.musicplayer.os10music.utils.NonSwipeableViewPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private String[] STAR = {"*"};

    private SmartTabLayout mTabLayout;
    private NonSwipeableViewPager mPager;
    private ArrayList<Fragment> mListFragment;
    private MyPagerAdapter mPagerAdapter;
    private SongsFragment songsFragment;
    private AlbumsFragment albumsFragment;
    private ArtistsFragment artistsFragment;
    private GenresFragment genresFragment;
    private FavoritesFragment favoritesFragment;

    private Intent playIntent;

    private MyApplication app;
    private DatabaseHelper db;
    private TextView mTitleText;
    private TextView mPlayingText;
    private TextView mTitleCenter;
    private TextView mCancelText;
    private ImageView mAddButton;

    private BroadcastReceiver receiver, receiverAlbumsClicked, receiverArtistsClicked;
    private IntentFilter filter, filterAlbumsClicked, filterArtistsClicked;

    public static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        app = (MyApplication) getApplication();
        db = app.getDatabase();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        1);
            } else {
                getAllSongs();
                ZergitasSDK.init(this);
            }
        } else {
            getAllSongs();
            ZergitasSDK.init(this);
        }

        mTabLayout = (SmartTabLayout) findViewById(R.id.tabs);
        mPager = (NonSwipeableViewPager) findViewById(R.id.tab_pager);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mPlayingText = (TextView) findViewById(R.id.playing_text);
        mTitleCenter = (TextView) findViewById(R.id.title_center);
        mCancelText = (TextView) findViewById(R.id.cancel_text);
        mAddButton = (ImageView) findViewById(R.id.add_button);

        mTitleText.setText(getResources().getString(R.string.songs_tab_name));

        songsFragment = new SongsFragment();
        albumsFragment = new AlbumsFragment();
        artistsFragment = new ArtistsFragment();
        genresFragment = new GenresFragment();
        favoritesFragment = new FavoritesFragment();

        mListFragment = new ArrayList<>();
        mListFragment.add(songsFragment);
        mListFragment.add(albumsFragment);
        mListFragment.add(artistsFragment);
        mListFragment.add(genresFragment);
        mListFragment.add(favoritesFragment);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), mListFragment);

        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(5);
        mPager.setCurrentItem(0);

        mTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {

            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tabs_icon, container, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.tab_image);
                TextView textView = (TextView) view.findViewById(R.id.tab_label);
                textView.setTextColor(getResources().getColorStateList(R.color.text_tab_indicator));

                switch (position) {
                    case 0:
                        imageView.setImageResource(R.drawable.img_song_tab);
                        textView.setText(getResources().getString(R.string.songs_tab_name));
                        break;
                    case 1:
                        imageView.setImageResource(R.drawable.img_album_tab);
                        textView.setText(getResources().getString(R.string.albums_tab_name));
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.img_artist_tab);
                        textView.setText(getResources().getString(R.string.artists_tab_name));
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.img_genres_tab);
                        textView.setText(getResources().getString(R.string.genres_tab_name));
                        break;
                    case 4:
                        imageView.setImageResource(R.drawable.img_favorite_tab);
                        textView.setText(getResources().getString(R.string.favorites_tab_name));
                        break;

                }

                return view;
            }
        });
        mTabLayout.setViewPager(mPager);

        mTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                switch (position) {
                    case 0:
                        mTitleText.setText(getResources().getString(R.string.songs_tab_name));
                        mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        mTitleText.setEnabled(false);
                        mTitleText.setVisibility(View.VISIBLE);
                        mPlayingText.setVisibility(View.VISIBLE);
                        mTitleCenter.setVisibility(View.GONE);
                        mCancelText.setVisibility(View.GONE);
                        mAddButton.setVisibility(View.GONE);
                        break;
                    case 1:
                        mTitleText.setText(getResources().getString(R.string.albums_tab_name));
                        mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        mTitleText.setEnabled(false);
                        mTitleText.setVisibility(View.VISIBLE);
                        mPlayingText.setVisibility(View.VISIBLE);
                        mTitleCenter.setVisibility(View.GONE);
                        mCancelText.setVisibility(View.GONE);
                        mAddButton.setVisibility(View.GONE);
                        albumsFragment.changeFragment(new AlbumsListFragment());
                        break;
                    case 2:
                        mTitleText.setText(getResources().getString(R.string.artists_tab_name));
                        mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        mTitleText.setEnabled(false);
                        mTitleText.setVisibility(View.VISIBLE);
                        mPlayingText.setVisibility(View.VISIBLE);
                        mTitleCenter.setVisibility(View.GONE);
                        mCancelText.setVisibility(View.GONE);
                        mAddButton.setVisibility(View.GONE);
                        artistsFragment.changeFragment(new ArtistsListFragment());
                        break;
                    case 3:
                        mTitleText.setText(getResources().getString(R.string.genres_tab_name));
                        mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        mTitleText.setEnabled(false);
                        mTitleText.setVisibility(View.VISIBLE);
                        mPlayingText.setVisibility(View.VISIBLE);
                        mTitleCenter.setVisibility(View.GONE);
                        mCancelText.setVisibility(View.GONE);
                        mAddButton.setVisibility(View.GONE);
                        break;
                    case 4:
                        mTitleText.setText(getResources().getString(R.string.favorites_tab_name));
                        mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        mTitleText.setEnabled(false);
                        mTitleText.setVisibility(View.VISIBLE);
                        mPlayingText.setVisibility(View.GONE);
                        mTitleCenter.setVisibility(View.GONE);
                        mCancelText.setVisibility(View.GONE);
                        mAddButton.setVisibility(View.VISIBLE);
                        favoritesFragment.changeFragment(new FavoritesShowFragment());
                        break;
                }
            }
        });

        filter = new IntentFilter("control_tabs");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTabLayout.getTabAt(intent.getExtras().getInt("tab_id")).performClick();
            }
        };
        registerReceiver(receiver, filter);

        filterAlbumsClicked = new IntentFilter("albums_item_clicked");
        receiverAlbumsClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                mTitleCenter.setVisibility(View.VISIBLE);
                mTitleCenter.setText(extras.getString("album_name"));
                mTitleText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back, 0, 0, 0);
                mTitleText.setEnabled(true);
            }
        };
        registerReceiver(receiverAlbumsClicked, filterAlbumsClicked);

        filterArtistsClicked = new IntentFilter("artists_item_clicked");
        receiverArtistsClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                mTitleCenter.setVisibility(View.VISIBLE);
                mTitleCenter.setText(extras.getString("artist_name"));
                mTitleText.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_back, 0, 0, 0);
                mTitleText.setEnabled(true);
            }
        };
        registerReceiver(receiverArtistsClicked, filterArtistsClicked);

        mPlayingText.setOnClickListener(this);
        mAddButton.setOnClickListener(this);
        mCancelText.setOnClickListener(this);
        mTitleText.setOnClickListener(this);

        Constant.increaseHitArea(mPlayingText);
        Constant.increaseHitArea(mAddButton);
        Constant.increaseHitArea(mCancelText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_text:
                Intent i = new Intent(MainActivity.this, PlayingActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);
                break;
            case R.id.add_button:
                mTitleText.setVisibility(View.GONE);
                mPlayingText.setVisibility(View.GONE);
                mTitleCenter.setVisibility(View.VISIBLE);
                mCancelText.setVisibility(View.VISIBLE);
                mAddButton.setVisibility(View.GONE);
                mTitleCenter.setText(getResources().getString(R.string.add_favorite));
                favoritesFragment.changeFragment(new FavoritesAddFragment());
                break;
            case R.id.cancel_text:
                mTitleText.setVisibility(View.VISIBLE);
                mPlayingText.setVisibility(View.GONE);
                mTitleCenter.setVisibility(View.GONE);
                mCancelText.setVisibility(View.GONE);
                mAddButton.setVisibility(View.VISIBLE);
                favoritesFragment.changeFragment(new FavoritesShowFragment());
                break;
            case R.id.title_text:
                if (mPager.getCurrentItem() == 1) {
                    albumsFragment.popBackStack();
                } else if (mPager.getCurrentItem() == 2) {
                    artistsFragment.popBackStack();
                }
                mTitleText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                mTitleText.setEnabled(false);
                mTitleCenter.setVisibility(View.GONE);
                break;
        }
    }

    //connect to the service
    /*private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(db.getAllSongs());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };*/

    @Override
    protected void onResume() {
        super.onResume();
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            //bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        active = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null){
            playIntent = new Intent(this, MusicService.class);
            //bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
        playIntent = null;
    }

    @Override
    protected void onDestroy() {
        /*stopService(playIntent);
        musicSrv = null;*/
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (receiverAlbumsClicked != null) {
            unregisterReceiver(receiverAlbumsClicked);
        }
        if (receiverArtistsClicked != null) {
            unregisterReceiver(receiverArtistsClicked);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getAllSongs();
                    Constant.triggerRebirth(this);
                    ZergitasSDK.init(this);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getAllSongs() {
        db.deleteAllSongs();
        Cursor cursor;
        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        cursor = getContentResolver().query(allsongsuri, STAR, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int song_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));

                    String song_name = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.TITLE));

                    String artist_id = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));

                    String song_artist = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));

                    String album_id = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    String album_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));

                    String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));
                    Log.d("full path", fullpath);

                    String duration = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DURATION));

                    Uri ART_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
                    Uri albumArtUri = ContentUris.withAppendedId(ART_CONTENT_URI, Integer.valueOf(album_id));

                    SongObject song = new SongObject(song_id, song_name, artist_id, song_artist, album_id, album_name, albumArtUri.toString(), fullpath, Integer.parseInt(duration));
                    db.addSong(song);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    public String getAlbumArt(int albumId) {
        String path = "";
        Cursor cursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[] {String.valueOf(albumId)},
                null);

        if (cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
        }

        return path;
    }
}
