package com.shellmonger.apps.familyphotos.models;

public class Album extends BaseModel {
    private String albumName;

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
