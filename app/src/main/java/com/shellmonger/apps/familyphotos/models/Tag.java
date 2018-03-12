package com.shellmonger.apps.familyphotos.models;

import java.util.UUID;

public class Tag extends BaseModel {
    private String tagId;
    private String tagName;

    public Tag() {
        tagId = UUID.randomUUID().toString();
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
