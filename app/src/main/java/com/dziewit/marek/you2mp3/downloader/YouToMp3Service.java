package com.dziewit.marek.you2mp3.downloader;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dziewit.marek.you2mp3.R;
import com.dziewit.marek.you2mp3.video_info.VideoInfoModel;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.google.gson.Gson;

import java.io.File;

public class YouToMp3Service extends IntentService implements AppManagedDownloadHandler {
    public static final String TAG = YouToMp3Service.class.getSimpleName();
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public static final int UPDATE_PROGRESS = 8344;

    public YouToMp3Service() {
        super("YouToMp3Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        VideoInfoModel model = getDataFromBundle(intent);
        String urlToDownload = model.getDownloadUrl();
        Log.d(YouToMp3Service.class.getSimpleName(), urlToDownload);
        buildNotification();
        DownloadFile(urlToDownload, model.getVideoTilte());
    }

    public void DownloadFile(String fileURL, String fileName) {

        String RootDir = Environment.getExternalStorageDirectory()
                + File.separator + "YouTuMp3Cache";
        File RootFile = new File(RootDir);

        try {
            if (RootFile.mkdir())
                new AppManagedDownload(this).run(fileURL, RootFile);
            else
                throw new Exception("Can't make this file, probably already exist");


            FFmpeg fFmpeg = FFmpeg.getInstance(getBaseContext());


            fFmpeg.execute("cmd", new FFmpegExecuteResponseHandler() {

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {

                }

                @Override
                public void onSuccess(String s) {

                }

                @Override
                public void onProgress(String s) {

                }

                @Override
                public void onFailure(String s) {

                }
            });

        } catch (Exception e) {
            doneMessage("Something went wrong file not downloaded :( ");
            Log.d("Error....", e.toString());
        }

    }

    private void buildNotification() {
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("You2Mp3")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_notification);
    }

    private void updateNotificationProgress(int max, int progress) {
        mBuilder.setProgress(max, progress, false);
        mBuilder.setOngoing(true);
        mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
    }

    private void doneMessage(String message) {
        mBuilder.setContentText(message)
                .setProgress(0, 0, false)
                .setOngoing(false);
        mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
    }

    private VideoInfoModel getDataFromBundle(Intent intent) {
        String jsonWithVideoInfo = intent.getStringExtra(VideoInfoModel.class.getSimpleName());
        return new Gson().fromJson(jsonWithVideoInfo, VideoInfoModel.class);
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onError(String errorMessage) {
        Log.d(TAG, errorMessage);
        doneMessage(errorMessage);
    }

    @Override
    public void onSuccess(String successMessage) {
        Log.d(TAG, successMessage);
        doneMessage(successMessage);
    }

    @Override
    public void onProgressUpdated(long progress, long max) {
        updateNotificationProgress((int) max, (int) progress);
    }
}