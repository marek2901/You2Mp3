package com.dziewit.marek.you2mp3.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dziewit.marek.you2mp3.R;
import com.dziewit.marek.you2mp3.SaveButtonInterface;
import com.dziewit.marek.you2mp3.downloader.YouToMp3Service;
import com.dziewit.marek.you2mp3.fragments.DetailsFragment;
import com.dziewit.marek.you2mp3.fragments.InputUrlFragment;
import com.dziewit.marek.you2mp3.video_info.VideoInfoModel;
import com.dziewit.marek.you2mp3.video_info.VideoInfoResultHandler;
import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class You2mp3 extends AppCompatActivity implements VideoInfoResultHandler, SaveButtonInterface {
    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you2mp3);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        commitStartFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null)
            setIntent(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            commitStartFragment(sharedText);
            intent.getExtras().remove(Intent.EXTRA_TEXT);
        }
    }

    private void commitStartFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, new InputUrlFragment())
                .commit();
    }

    private void commitStartFragment(String url) {
        InputUrlFragment fragment = new InputUrlFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VideoInfoModel.class.getSimpleName(), url);
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_you2mp3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, YouMp3SettingsActivity.class));

        return true;
    }

    @Override
    public void succes(VideoInfoModel infoModel) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(VideoInfoModel.class.getSimpleName(), new Gson().toJson(infoModel));
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment)
                .commit();
    }

    @Override
    public void saveButtonPressed(VideoInfoModel model) {
        commitStartFragment();

        Intent intent = new Intent(this, YouToMp3Service.class);
        intent.putExtra(VideoInfoModel.class.getSimpleName(), new Gson().toJson(model));
        startService(intent);

        Toast.makeText(this, getString(R.string.download_started_toast), Toast.LENGTH_SHORT).show();
    }
}
