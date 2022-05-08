package com.example.bookapp;

public class ModelCategory {
    String id,uid,timestamp,category;

    public ModelCategory(String id, String category, String uid, String timestamp) {
        this.id = id;
        this.uid = uid;
        this.timestamp = timestamp;
        this.category = category;
    }

    public ModelCategory() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
