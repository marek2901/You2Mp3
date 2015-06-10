package com.dziewit.marek.you2mp3.video_info;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;


public class VideoInfoAsyncProvider extends AsyncTask<String, Void, Void> {

    public static final String TAG = VideoInfoAsyncProvider.class.getSimpleName();
    private final VideoInfoResultHandler handler;
    private VideoInfoModel model;

    public VideoInfoAsyncProvider(VideoInfoResultHandler handler) {
        this.handler = handler;
        model = new VideoInfoModel();
    }

    @Override
    protected Void doInBackground(String... urls) {
        try {
            String videoID = videoIdProvider(urls[0]);

            String pageContent = urlContentToString("https://www.youtube.com/get_video_info?&video_id=" + videoID + "&el=vevo&ps=default&eurl=&gl=US&hl=en");
            QueryStringParser parsedContent = new QueryStringParser(pageContent);

            String stringWithStreamMap = parsedContent.getParameterValue("url_encoded_fmt_stream_map");
            QueryStringParser streamMapWithUrl = new QueryStringParser(stringWithStreamMap);

            model.setDownloadUrl(streamMapWithUrl.getParameterValue("url"));
            model.setVideoTilte(parsedContent.getParameterValue("title"));
        } catch (IOException | NullPointerException e) {
            model.setIsNotOk();
        }

        return null;
    }

    private String videoIdProvider(String youtubeUrl) {
        try {
            String videoId = "https://www.youtube.com/watch?v=88rXR9fOFDE";

            videoId = videoId.split("\\?")[1];
            videoId = videoId.split("&")[0];
            videoId = videoId.split("=")[1];

            return videoId;
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "videoprovider failed");
            return null;
        }
    }

    private String urlContentToString(String url) throws IOException {
        URL urlObject = new URL(url);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(urlObject.openStream()));
        StringBuilder builder = new StringBuilder();
        String line;

        // For every line in the file, append it to the string builder
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        reader.close();
        return builder.toString();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        this.handler.succes(model);
    }
}
