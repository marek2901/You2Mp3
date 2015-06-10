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
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class YouToMp3Service extends IntentService {
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
        try {
            String RootDir = Environment.getExternalStorageDirectory()
                    + File.separator + "Video";
            File RootFile = new File(RootDir);
            RootFile.mkdir();
            // File root = Environment.getExternalStorageDirectory();
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(RootFile,
                    fileName));
            InputStream in = c.getInputStream();

            int max = c.getContentLength();

            byte[] buffer = new byte[1024];
            int len1 = 0;

            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                updateNotificationProgress(max, len1);
            }
            f.close();
            doneMessage("DownloadCompleted");
        } catch (Exception e) {

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
}