package com.example.newfestivalpost.ModelRetrofit;

import com.google.gson.annotations.SerializedName;

public class VideoHomeModel {

    @SerializedName("result")
    private String result;

    @SerializedName("message")
    private String message;

    @SerializedName("records")
    private VideoHomeRecords records;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VideoHomeRecords getRecords() {
        return records;
    }

    public void setRecords(VideoHomeRecords records) {
        this.records = records;
    }
}
