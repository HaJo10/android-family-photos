package com.shellmonger.apps.familyphotos.models;

import java.util.UUID;

public class Album extends BaseModel {
    private String albumId;
    private String albumName;

    public Album() {
        albumId = UUID.randomUUID().toString();
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
