package com.dziewit.marek.you2mp3.downloader;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeMPGParser;
import com.github.axet.vget.vhs.YoutubeInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppManagedDownload {

    private AppManagedDownloadHandler handler;

    VideoInfo info;
    long last;

    public AppManagedDownload(AppManagedDownloadHandler handler) {
        this.handler = handler;
    }

    public void run(String url, File path, final String filename) {
        try {
            AtomicBoolean stop = new AtomicBoolean(false);
            Runnable notify = new Runnable() {
                @Override
                public void run() {
                    VideoInfo i1 = info;
                    DownloadInfo i2 = i1.getInfo();
                    i2.setContentFilename(filename);

                    switch (i1.getState()) {
                        case EXTRACTING:
                        case EXTRACTING_DONE:
                        case DONE:
                            if (i1 instanceof YoutubeInfo) {
                                YoutubeInfo i = (YoutubeInfo) i1;
                                handler.onDownloadSuccess(i1.getTitle() + " " + i1.getState() + " " + i.getVideoQuality(), i1.getTitle());
                            } else if (i1 instanceof VimeoInfo) {
                                VimeoInfo i = (VimeoInfo) i1;
                                handler.onDownloadSuccess(i1.getState() + " " + i.getVideoQuality(), i1.getTitle());
                            } else {
                                handler.onError("downloading unknown quality");
                            }
                            break;
                        case RETRYING:
                            handler.onMessage(i1.getState() + " " + i1.getDelay());
                            break;
                        case DOWNLOADING:
                            long now = System.currentTimeMillis();
                            if (now - 1000 > last) {
                                last = now;

                                String parts = "";

                                List<Part> pp = i2.getParts();
                                if (pp != null) {
                                    // multipart download
                                    for (Part p : pp) {
                                        if (p.getState().equals(States.DOWNLOADING)) {
                                            parts += String.format("Part#%d(%.2f) ", p.getNumber(), p.getCount()
                                                    / (float) p.getLength());
                                        }
                                    }
                                }

                                handler.onProgressUpdated(i2.getCount(), i2.getLength());
                            }
                            break;
                        default:
                            break;
                    }
                }
            };

            URL web = new URL(url);

            VGetParser user;

            // download mp4 format only, fail if non exist
            user = new YouTubeMPGParser();

            // create proper videoinfo to keep specific video information
            info = user.info(web);

            VGet v = new VGet(info, path);
//            v.extract(user, stop, notify);

            v.download(user, stop, notify);
        } catch (Exception e) {
            handler.onError(e.getMessage());
        }
    }
}