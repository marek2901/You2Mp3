package com.dziewit.marek.you2mp3.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.dziewit.marek.you2mp3.R;
import com.dziewit.marek.you2mp3.SaveButtonInterface;
import com.dziewit.marek.you2mp3.video_info.VideoInfoModel;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DetailsFragment extends Fragment {


    @InjectView(R.id.video_title_text_edit_text)
    EditText titleEditText;
    private VideoInfoModel model;
    private SaveButtonInterface savePressedHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, null);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            savePressedHandler = (SaveButtonInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getLocalClassName()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @InjectView(R.id.thumbnail_preview)
    ImageView thumbnailView;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = new Gson()
                .fromJson(
                        this.getArguments().getString(VideoInfoModel.class.getSimpleName())
                        , VideoInfoModel.class
                );
        titleEditText.setText(model.getVideoTilte());

        Picasso.with(getActivity())
                .load(model.getThumbnail_url())
                .fit()
                .into(thumbnailView);
    }

    @OnClick(R.id.save_button)
    void saveFile() {
        String newTitle = titleEditText.getText().toString();
        if (!newTitle.isEmpty())
            model.setVideoTilte(newTitle);

        savePressedHandler.saveButtonPressed(model);
    }
}
