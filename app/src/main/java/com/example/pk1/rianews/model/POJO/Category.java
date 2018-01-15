package com.example.pk1.rianews.model.POJO;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Category extends RealmObject implements Serializable {
    @PrimaryKey
    private String url;
    private String name;

    public Category(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public Category() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
