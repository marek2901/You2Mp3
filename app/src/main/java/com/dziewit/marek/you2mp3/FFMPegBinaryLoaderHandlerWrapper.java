package com.dziewit.marek.you2mp3;

import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;

public abstract class FFMPegBinaryLoaderHandlerWrapper implements FFmpegLoadBinaryResponseHandler {
    @Override
    public void onFailure() {
        binaryLoadFailed();
    }

    @Override
    public void onSuccess() {
        binaryLoaded();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    public abstract void binaryLoaded();

    public abstract void binaryLoadFailed();
}
