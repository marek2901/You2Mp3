package com.dziewit.marek.you2mp3.video_info;


import com.google.gson.annotations.SerializedName;

public class VideoInfoModel {
    private boolean isOk;

    public VideoInfoModel() {
        this.isOk = true;
    }

    @SerializedName("title")
    private String videoTilte;

    private String downloadUrl;

    private String thumbnail_url;

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVideoTilte() {
        String trimmedVideoTitle = videoTilte.replaceAll("\\s+", "");
        trimmedVideoTitle = trimmedVideoTitle.replaceAll("[^a-zA-Z0-9.-]", "_");

        return trimmedVideoTitle;
    }

    public void setVideoTilte(String videoTilte) {
        this.videoTilte = videoTilte;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setIsOk() {
        this.isOk = true;
    }

    public void setIsNotOk() {
        this.isOk = false;
    }
}
