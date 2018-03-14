package com.shellmonger.apps.familyphotos.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Photo extends BaseModel {
    private String photoId;
    private String imageName;
    private String caption;
    private String albumId;
    private List<String> tags;
    private boolean hidden;
    private long created;

    public Photo() {
        photoId = UUID.randomUUID().toString();
        tags = new ArrayList<>();
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

