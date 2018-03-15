package com.shellmonger.apps.familyphotos.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Photo extends BaseModel {
    private Bitmap picture;
    private String caption;
    private String albumId;
    private List<String> tags;
    private boolean hidden;
    private long created;

    public Photo() {
        super();
        tags = new ArrayList<>();
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}

