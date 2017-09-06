package com.workspace.playmusicsample.data.model;

/**
 * Created by workspace on 06/09/2017.
 */

public class Song {
    private long id;
    private String title;
    private String artist;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public Song(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
