package com.dziewit.marek.you2mp3.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.dziewit.marek.you2mp3.R;

import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class YouMp3SettingsActivity extends AppCompatActivity implements DirectoryChooserFragment.OnFragmentInteractionListener {

    public static final String PREFS_DIR_VALUE = "prefs_dir_value";
    public static final String PREFERENCES_NAME = "You2Mp3Prefferences";

    private DirectoryChooserFragment mDialog;

    @InjectView(R.id.file_dir)
    TextView dirTextView;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_mp3_settings);
        ButterKnife.inject(YouMp3SettingsActivity.this);
        preferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        dirTextView.setText(restoreData());

        mDialog = DirectoryChooserFragment.newInstance("you2mp3music", null);
    }

    @OnClick(R.id.button_change_dir)
    void changeDir() {
        mDialog.show(getFragmentManager(), null);
    }


    private void handleDirectoryChoice(String path) {
        saveData(path);
        dirTextView.setText(path);
    }

    private String restoreData() {
        return preferences.getString(PREFS_DIR_VALUE, "");
    }

    private void saveData(String path) {
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putString(PREFS_DIR_VALUE, path);
        preferencesEditor.apply();
    }


    @Override
    public void onSelectDirectory(@NonNull String s) {
        handleDirectoryChoice(s);
        mDialog.dismiss();
        Toast.makeText(YouMp3SettingsActivity.this,
                getString(R.string.dir_succes_message), Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void onCancelChooser() {
        mDialog.dismiss();
    }
}
