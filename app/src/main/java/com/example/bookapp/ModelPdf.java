package com.example.bookapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pdf")
public class ModelPdf {
    @PrimaryKey
  @NonNull
    String id ;
    String    uid;
    String  description;
    String   name;
    String    timestamp;
    String    categoryId;
    String    url;
    String   category;
    boolean isFavorite;
    public ModelPdf() {
    }

    public ModelPdf(String id, String uid, String description, String name, String timestamp, String categoryId, String url, String category, boolean isFavorite) {


        this.id = id;
        this.uid = uid;
        this.description = description;
        this.name = name;
        this.timestamp = timestamp;
        this.categoryId = categoryId;
        this.url = url;
        this.category = category;
        this.isFavorite = isFavorite;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
