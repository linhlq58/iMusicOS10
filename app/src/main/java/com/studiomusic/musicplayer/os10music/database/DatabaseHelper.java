package com.studiomusic.musicplayer.os10music.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.studiomusic.musicplayer.os10music.objects.AlbumObject;
import com.studiomusic.musicplayer.os10music.objects.ArtistObject;
import com.studiomusic.musicplayer.os10music.objects.SongObject;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "iMusicDB";

    private static final String TABLE_SONGS = "songs";
    private static final String KEY_SONG_ID = "song_id";
    private static final String KEY_SONG_NAME = "song_name";
    private static final String KEY_SONG_ARTIST_ID = "song_artist_id";
    private static final String KEY_SONG_ARTIST = "song_artist";
    private static final String KEY_SONG_ALBUM_ID = "song_album_id";
    private static final String KEY_SONG_ALBUM = "song_album";
    private static final String KEY_SONG_ALBUM_ART_URI = "song_album_art_uri";
    private static final String KEY_SONG_FULLPATH = "song_fullpath";
    private static final String KEY_SONG_DURATION = "song_duration";
    private static final String KEY_SONG_FAVORITE = "song_favorite";

    private static final String TABLE_FAVORITE_SONGS = "favorite_songs";
    private static final String KEY_FAVORITE_ID = "favorite_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SONGS + " ("
                + KEY_SONG_ID + " INTEGER PRIMARY KEY, "
                + KEY_SONG_NAME + " TEXT, "
                + KEY_SONG_ARTIST_ID + " TEXT, "
                + KEY_SONG_ARTIST + " TEXT, "
                + KEY_SONG_ALBUM_ID + " TEXT, "
                + KEY_SONG_ALBUM + " TEXT, "
                + KEY_SONG_ALBUM_ART_URI + " TEXT, "
                + KEY_SONG_FULLPATH + " TEXT, "
                + KEY_SONG_DURATION + " INTEGER, "
                + KEY_SONG_FAVORITE + " INTEGER)";

        String CREATE_FAVORITE_SONGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE_SONGS + " ("
                + KEY_FAVORITE_ID + " INTEGER)";

        db.execSQL(CREATE_SONGS_TABLE);
        db.execSQL(CREATE_FAVORITE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_SONGS);

        this.onCreate(db);
    }

    public void addSong(SongObject songObject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_ID, songObject.getId());
        values.put(KEY_SONG_NAME, songObject.getName());
        values.put(KEY_SONG_ARTIST_ID, songObject.getArtist_id());
        values.put(KEY_SONG_ARTIST, songObject.getArtist());
        values.put(KEY_SONG_ALBUM_ID, songObject.getAlbum_id());
        values.put(KEY_SONG_ALBUM, songObject.getAlbum());
        values.put(KEY_SONG_ALBUM_ART_URI, songObject.getAlbumArtUri());
        values.put(KEY_SONG_FULLPATH, songObject.getFullpath());
        values.put(KEY_SONG_DURATION, songObject.getDuration());
        values.put(KEY_SONG_FAVORITE, songObject.getIsFavorite());

        db.insert(TABLE_SONGS, null, values);

        db.close();
    }

    public void addFavoriteSong(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FAVORITE_ID, id);

        db.insert(TABLE_FAVORITE_SONGS, null, values);

        db.close();
    }

    public ArrayList<SongObject> getAllSongs() {
        ArrayList<SongObject> listSongs = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SongObject song = new SongObject();
                song.setId(cursor.getInt(0));
                song.setName(cursor.getString(1));
                song.setArtist_id(cursor.getString(2));
                song.setArtist(cursor.getString(3));
                song.setAlbum_id(cursor.getString(4));
                song.setAlbum(cursor.getString(5));
                song.setAlbumArtUri(cursor.getString(6));
                song.setFullpath(cursor.getString(7));
                song.setDuration(cursor.getInt(8));
                song.setIsFavorite(cursor.getInt(9));

                listSongs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listSongs;
    }

    public ArrayList<SongObject> getAllFavoritesSongs() {
        ArrayList<SongObject> listSongs = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SONGS + " s JOIN " + TABLE_FAVORITE_SONGS + " f"
                + " WHERE s.song_id = f.favorite_id";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SongObject song = new SongObject();
                song.setId(cursor.getInt(0));
                song.setName(cursor.getString(1));
                song.setArtist_id(cursor.getString(2));
                song.setArtist(cursor.getString(3));
                song.setAlbum_id(cursor.getString(4));
                song.setAlbum(cursor.getString(5));
                song.setAlbumArtUri(cursor.getString(6));
                song.setFullpath(cursor.getString(7));
                song.setDuration(cursor.getInt(8));
                song.setIsFavorite(cursor.getInt(9));

                listSongs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listSongs;
    }

    public ArrayList<AlbumObject> getAllAlbums() {
        ArrayList<AlbumObject> listAlbums = new ArrayList<>();

        String query = "SELECT DISTINCT " + KEY_SONG_ALBUM_ID + ", " + KEY_SONG_ALBUM + " FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                AlbumObject album = new AlbumObject();
                album.setId(cursor.getString(0));
                album.setName(cursor.getString(1));

                listAlbums.add(album);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listAlbums;
    }

    public ArrayList<ArtistObject> getAllArtists() {
        ArrayList<ArtistObject> listArtists = new ArrayList<>();

        String query = "SELECT DISTINCT " + KEY_SONG_ARTIST_ID + ", " + KEY_SONG_ARTIST + " FROM " + TABLE_SONGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                ArtistObject artist = new ArtistObject();
                artist.setId(cursor.getString(0));
                artist.setName(cursor.getString(1));

                listArtists.add(artist);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listArtists;
    }

    public ArrayList<SongObject> getAllSongsByAlbumId(String id) {
        ArrayList<SongObject> listSongs = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SONGS
                + " WHERE " + KEY_SONG_ALBUM_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SongObject song = new SongObject();
                song.setId(cursor.getInt(0));
                song.setName(cursor.getString(1));
                song.setArtist_id(cursor.getString(2));
                song.setArtist(cursor.getString(3));
                song.setAlbum_id(cursor.getString(4));
                song.setAlbum(cursor.getString(5));
                song.setAlbumArtUri(cursor.getString(6));
                song.setFullpath(cursor.getString(7));
                song.setDuration(cursor.getInt(8));
                song.setIsFavorite(cursor.getInt(9));

                listSongs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listSongs;
    }

    public ArrayList<SongObject> getAllSongsByArtistId(String id) {
        ArrayList<SongObject> listSongs = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_SONGS
                + " WHERE " + KEY_SONG_ARTIST_ID + " = " + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SongObject song = new SongObject();
                song.setId(cursor.getInt(0));
                song.setName(cursor.getString(1));
                song.setArtist_id(cursor.getString(2));
                song.setArtist(cursor.getString(3));
                song.setAlbum_id(cursor.getString(4));
                song.setAlbum(cursor.getString(5));
                song.setAlbumArtUri(cursor.getString(6));
                song.setFullpath(cursor.getString(7));
                song.setDuration(cursor.getInt(8));
                song.setIsFavorite(cursor.getInt(9));

                listSongs.add(song);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return listSongs;
    }

    public int updateSong(SongObject song) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_ID, song.getId());
        values.put(KEY_SONG_NAME, song.getName());
        values.put(KEY_SONG_ARTIST_ID, song.getArtist_id());
        values.put(KEY_SONG_ARTIST, song.getArtist());
        values.put(KEY_SONG_ALBUM_ID, song.getAlbum_id());
        values.put(KEY_SONG_ALBUM, song.getAlbum());
        values.put(KEY_SONG_ALBUM_ART_URI, song.getAlbumArtUri());
        values.put(KEY_SONG_FULLPATH, song.getFullpath());
        values.put(KEY_SONG_DURATION, song.getDuration());
        values.put(KEY_SONG_FAVORITE, song.getIsFavorite());

        int i = db.update(TABLE_SONGS, values, KEY_SONG_ID + " = ?",
                new String[]{String.valueOf(song.getId())});

        db.close();

        return i;
    }

    public void deleteFavoriteSong(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_FAVORITE_SONGS, KEY_FAVORITE_ID + " = " + id, null);

        db.close();
    }

    public void deleteAllSongs() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SONGS, null, null);

        db.close();
    }
}
