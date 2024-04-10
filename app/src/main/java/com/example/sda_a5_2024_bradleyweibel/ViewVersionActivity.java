package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ViewVersionActivity extends AppCompatActivity
{
    private TextView songNameTxt, versionNameTxt, versionDescriptionTxt, versionLyricsTxt;
    private Button editVersionBtn, deleteVersionBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;
    private VersionModal versionData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_version);

        // Getting song name which we passed in the adapter class
        Integer versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        Integer versionSongId = getIntent().getIntExtra(StringHelper.VersionData_Intent_Song_ID, 0);
        String versionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);

        // Attaching variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameTxt = findViewById(R.id.idTxtVersionName);
        versionDescriptionTxt = findViewById(R.id.idTxtVersionDescription);
        versionLyricsTxt = findViewById(R.id.idTxtVersionLyrics);
        editVersionBtn = findViewById(R.id.idBtnEditVersion);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        // Initializing db Handler
        dbHandler = new DBHandler(ViewVersionActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        // Get song name
        String songName = dbHandler.getSongName(versionSongId);
        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameTxt.setText(versionName);
        versionDescriptionTxt.setText(versionData.getVersionDescription());
        versionLyricsTxt.setText(versionData.getVersionLyrics());


        // Edit the song's name button is clicked
        //editVersionBtn.setOnClickListener(new View.OnClickListener()
        //{
        //    @Override
        //    public void onClick(View v)
        //    {
                // Opening a new activity via a intent
        //        Intent i = new Intent(ViewVersionActivity.this, EditSongActivity.class);
                // Passing song name through intent
        //        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        //        startActivity(i);
        //    }
        //});

        // Back to song and versions screen
        backToSongAndVersionsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(ViewVersionActivity.this, ViewSongAndVersionsActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });
    }
}
