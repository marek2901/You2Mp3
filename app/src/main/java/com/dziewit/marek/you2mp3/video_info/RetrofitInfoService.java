package com.dziewit.marek.you2mp3.video_info;

import retrofit.http.GET;
import retrofit.http.Query;

public interface RetrofitInfoService {

    @GET("/oembed?format=json")
    VideoInfoModel getVideoInfo(@Query("url") String url);
}
