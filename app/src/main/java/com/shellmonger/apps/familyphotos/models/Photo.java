package com.shellmonger.apps.familyphotos.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Photo extends BaseModel {
    private String photoId;
    private String imageName;
    private String caption;
    private Album album;
    private List<Tag> tags;
    private boolean hidden;
    private long created;

    public Photo() {
        photoId = UUID.randomUUID().toString();
        tags = new ArrayList<Tag>();
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
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

