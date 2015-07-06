package com.dziewit.marek.you2mp3.video_info;


import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

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
        return replaceBadChars(videoTilte);
    }

    private String replaceBadChars(String f) {
        String replace = "";
        f = f.replaceAll("/", replace);
        f = f.replaceAll("\\\\", replace);
        f = f.replaceAll(":", replace);
        f = f.replaceAll("\\?", replace);
        f = f.replaceAll("\\\"", replace);
        f = f.replaceAll("\\*", replace);
        f = f.replaceAll("<", replace);
        f = f.replaceAll(">", replace);
        f = f.replaceAll("\\|", replace);
        f = f.trim();
        f = StringUtils.removeEnd(f, ".");
        f = f.trim();

        String ff;
        while (!(ff = f.replaceAll("  ", " ")).equals(f)) {
            f = ff;
        }

        return f;
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
