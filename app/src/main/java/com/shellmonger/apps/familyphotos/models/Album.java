package com.shellmonger.apps.familyphotos.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Album extends BaseModel {
    private String albumId;
    private String albumName;
    private List<Photo> photos;

    public Album() {
        albumId = UUID.randomUUID().toString();
        photos = new ArrayList<Photo>();
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

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
