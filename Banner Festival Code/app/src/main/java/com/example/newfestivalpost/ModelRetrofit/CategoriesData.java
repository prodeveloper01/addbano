package com.example.newfestivalpost.ModelRetrofit;

import com.google.gson.annotations.SerializedName;

public class CategoriesData {

    @SerializedName("id")
    private String id;
    @SerializedName("language")
    private String language;
    @SerializedName("name")
    public String name;
    @SerializedName("image")
    public String image;

    @SerializedName("status")
    public String status;
    @SerializedName("image_url")
    public String image_url;

    @SerializedName("detail_display")
    public String detail_display;

    @SerializedName("detail_message")
    public String detail_message;

    public String getDetail_display() {
        return detail_display;
    }

    public void setDetail_display(String detail_display) {
        this.detail_display = detail_display;
    }

    public String getDetail_message() {
        return detail_message;
    }

    public void setDetail_message(String detail_message) {
        this.detail_message = detail_message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
