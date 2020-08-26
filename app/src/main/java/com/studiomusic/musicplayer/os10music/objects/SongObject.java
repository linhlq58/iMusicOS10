package com.studiomusic.musicplayer.os10music.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class SongObject implements Parcelable {
    private int id;
    private String name;
    private String artist_id;
    private String artist;
    private String album_id;
    private String album;
    private String albumArtUri;
    private String fullpath;
    private int duration;
    private int isFavorite = 0;

    public SongObject(int id, String name, String artist_id, String artist, String album_id, String album, String albumArtUri, String fullpath, int duration) {
        this.id = id;
        this.name = name;
        this.artist_id = artist_id;
        this.artist = artist;
        this.album_id = album_id;
        this.album = album;
        this.albumArtUri = albumArtUri;
        this.fullpath = fullpath;
        this.duration = duration;
    }

    public SongObject() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumArtUri() {
        return albumArtUri;
    }

    public void setAlbumArtUri(String albumArtUri) {
        this.albumArtUri = albumArtUri;
    }

    public String getFullpath() {
        return fullpath;
    }

    public void setFullpath(String fullpath) {
        this.fullpath = fullpath;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(artist_id);
        dest.writeString(artist);
        dest.writeString(album_id);
        dest.writeString(album);
        dest.writeString(albumArtUri);
        dest.writeString(fullpath);
        dest.writeInt(duration);
        dest.writeInt(isFavorite);
    }

    public static final Parcelable.Creator<SongObject> CREATOR
            = new Parcelable.Creator<SongObject>() {
        public SongObject createFromParcel(Parcel in) {
            return new SongObject(in);
        }

        public SongObject[] newArray(int size) {
            return new SongObject[size];
        }
    };

    private SongObject(Parcel in) {
        id = in.readInt();
        name = in.readString();
        artist_id = in.readString();
        artist = in.readString();
        album_id = in.readString();
        album = in.readString();
        albumArtUri = in.readString();
        fullpath = in.readString();
        duration = in.readInt();
        isFavorite  = in.readInt();
    }
}
