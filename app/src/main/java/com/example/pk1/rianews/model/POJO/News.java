package com.example.pk1.rianews.model.POJO;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class News extends RealmObject implements Serializable {
    @PrimaryKey
    private String url;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String fullSizeImageUrl;
    private String category;
    private String text;

    public News(String url, String title, String subtitle, String imageUrl, String category) {
        this.url = url;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public News() {
    }

    public String getFullSizeImageUrl() {
        return fullSizeImageUrl;
    }

    public void setFullSizeImageUrl(String fullSizeImageUrl) {
        this.fullSizeImageUrl = fullSizeImageUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
