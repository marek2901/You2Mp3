package com.dziewit.marek.you2mp3.downloader;


public interface AppManagedDownloadHandler {
    void onMessage(String message);

    void onError(String errorMessage);

    void onDownloadSuccess(String successMessage, String fileName);

    void onProgressUpdated(long progress, long max);
}
