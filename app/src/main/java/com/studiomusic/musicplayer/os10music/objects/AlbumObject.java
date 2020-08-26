package com.studiomusic.musicplayer.os10music.objects;

/**
 * Created by Linh Lee on 11/14/2016.
 */
public class AlbumObject {
    private String id;
    private String name;

    public AlbumObject(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public  AlbumObject() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
