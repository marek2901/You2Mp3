package com.dziewit.marek.you2mp3.downloader;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dziewit.marek.you2mp3.R;
import com.dziewit.marek.you2mp3.activities.YouMp3SettingsActivity;
import com.dziewit.marek.you2mp3.video_info.VideoInfoModel;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

public class YouToMp3Service extends IntentService implements AppManagedDownloadHandler, FFmpegExecuteResponseHandler {
    public static final String TAG = YouToMp3Service.class.getSimpleName();
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;

    public static final int UPDATE_PROGRESS = 8344;
    private File rootFile;

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void DownloadFile(String fileURL, String fileName) {

        String RootDir = Environment.getExternalStorageDirectory()
                + File.separator + "YouTuMp3Cache";
        rootFile = new File(RootDir);

        try {
            FileUtils.deleteDirectory(rootFile);
            rootFile.mkdir();
            new AppManagedDownload(this).run(fileURL, rootFile, fileName);
        } catch (Exception e) {
            doneMessage("Something went wrong file not downloaded :( ");
            Log.d("Error....", e.toString());
            onFinish();
        }

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
    public void onDownloadSuccess(final String successMessage, final String filename) {
        Log.d(TAG, successMessage);
//        onGoingMessage(successMessage);
        doneMessage(successMessage);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Sleep interrupted " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startConversion(filename);
            }
        }.execute();
    }

    @Override
    public void onProgressUpdated(long progress, long max) {
        updateNotificationProgress((int) max, (int) progress);
    }

    private void startConversion(String filename) {
        SharedPreferences preferences =
                getSharedPreferences(YouMp3SettingsActivity.PREFERENCES_NAME, Activity.MODE_PRIVATE);

        filename = filename.replaceAll(" ", Matcher.quoteReplacement("\\"));

        String targetBaseDir = preferences.getString(YouMp3SettingsActivity.PREFS_DIR_VALUE, null);
        String source = rootFile.getAbsolutePath() + File.separator + filename + ".mp4";
        String target = targetBaseDir + File.separator + filename + ".mp3";

        FFmpeg fFmpeg = FFmpeg.getInstance(getBaseContext());

//        ffmpeg -i video.mp4 -b:a 192K -vn music.mp3
        String cmd = "-i " + source + " -b:a 192K -vn " + target;
        try {
            fFmpeg.execute(cmd, this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            doneMessage("Cant convert");
            onFinish();
        }

    }

    @Override
    public void onSuccess(String s) {
        doneMessage(s);
        try {
            FileUtils.deleteDirectory(rootFile);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            onFinish();
        }
    }

    @Override
    public void onProgress(String s) {
        onGoingMessage(s);
    }

    @Override
    public void onFailure(String s) {
        doneMessage(s);
        try {
            FileUtils.deleteDirectory(rootFile);
        } catch (IOException e) {
            onFinish();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }


    //Helper Methods for notifying User
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

    private void onGoingMessage(String message) {
        mBuilder.setContentText(message)
                .setProgress(0, 0, false)
                .setOngoing(true);
        mNotifyManager.notify(UPDATE_PROGRESS, mBuilder.build());
    }

    private VideoInfoModel getDataFromBundle(Intent intent) {
        String jsonWithVideoInfo = intent.getStringExtra(VideoInfoModel.class.getSimpleName());
        return new Gson().fromJson(jsonWithVideoInfo, VideoInfoModel.class);
    }

}