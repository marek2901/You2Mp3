package com.dziewit.marek.you2mp3.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dziewit.marek.you2mp3.R;
import com.dziewit.marek.you2mp3.video_info.VideoInfoAsyncProvider;
import com.dziewit.marek.you2mp3.video_info.VideoInfoModel;
import com.dziewit.marek.you2mp3.video_info.VideoInfoResultHandler;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InputUrlFragment extends Fragment implements VideoInfoResultHandler {
    VideoInfoResultHandler handler;

    @InjectView(R.id.editTextUrl)
    EditText urlEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_url, null);
        ButterKnife.inject(this, view);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadVideoInfoIfBundleUrlProvided();
    }

    private void loadVideoInfoIfBundleUrlProvided() {
        try {
            String url = this.getArguments().getString(VideoInfoModel.class.getSimpleName());
            loadVideoData(url);
        } catch (Exception e) {
            //nothing get nothing returned
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            handler = (VideoInfoResultHandler) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @OnClick(R.id.download_button)
    public void downloadInfo() {
        String url = urlEditText.getText().toString();
        loadVideoData(url);
    }

    private void loadVideoData(String url) {
        if (!url.isEmpty()) {
            VideoInfoAsyncProvider provider = new VideoInfoAsyncProvider(this);
            provider.execute(url);
            startProgressAnimation();
        } else
            Toast.makeText(getActivity(), "Put someUrlHere", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void succes(VideoInfoModel infoModel) {
        if (infoModel.isOk()) {
            handler.succes(infoModel);
        } else {
            Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_LONG).show();
        }
        stopProgressAnimation();
    }

    @InjectView(R.id.progress_container)
    FrameLayout progressBar;
    @InjectView(R.id.text_linear_container)
    LinearLayout linearTextContainer;

    private void startProgressAnimation() {
        progressBar.setVisibility(View.VISIBLE);
        linearTextContainer.setVisibility(View.INVISIBLE);
    }

    private void stopProgressAnimation() {
        progressBar.setVisibility(View.GONE);
        linearTextContainer.setVisibility(View.VISIBLE);
    }
}
