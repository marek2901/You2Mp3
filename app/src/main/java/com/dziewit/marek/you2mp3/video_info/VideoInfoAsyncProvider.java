package com.dziewit.marek.you2mp3.video_info;

import android.os.AsyncTask;
import android.util.Log;

import retrofit.RestAdapter;


public class VideoInfoAsyncProvider extends AsyncTask<String, Void, VideoInfoModel> {

    public static final String TAG = VideoInfoAsyncProvider.class.getSimpleName();
    private final VideoInfoResultHandler handler;

    public VideoInfoAsyncProvider(VideoInfoResultHandler handler) {
        this.handler = handler;
    }

    @Override
    protected VideoInfoModel doInBackground(String... urls) {
        urls[0] = "https://www.youtube.com/watch?v=LDZX4ooRsWs";
        VideoInfoModel model = new VideoInfoModel();
        try {

            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint("http://www.youtube.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build();

            RetrofitInfoService service = adapter.create(RetrofitInfoService.class);

            model = service.getVideoInfo(urls[0]);

            model.setDownloadUrl(urls[0]);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            model.setIsNotOk();
        }

        return model;
    }

    @Override
    protected void onPostExecute(VideoInfoModel model) {
        super.onPostExecute(model);
        this.handler.succes(model);
    }
}
