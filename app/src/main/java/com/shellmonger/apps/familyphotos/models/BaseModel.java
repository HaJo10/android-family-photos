package com.shellmonger.apps.familyphotos.models;

import java.util.UUID;

/**
 * The base model for all models.  Contains the references
 * needed for synchronization.
 */
public class BaseModel {
    private String id;
    private long updated;
    private boolean deleted;
    private byte[] version;

    public BaseModel() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }

    public boolean equals(BaseModel v) {
        if (v == null) return false;
        return (v.getId().equals(id));
    }
}
