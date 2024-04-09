package com.example.sda_a5_2024_bradleyweibel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewSongAndVersionsActivity extends AppCompatActivity
{
    private TextView songNameTxt;
    private Button editSongBtn, backToHomeBtn;
    private ArrayList<VersionModal> versionModalArrayList;
    private DBHandler dbHandler;
    private VersionRVAdapter versionRVAdapter;
    private RecyclerView versionsRV;
    private SharedPreferences songData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_song_and_versions);

        // Getting data which we passed in our adapter class
        String songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);

        // Attaching variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        songNameTxt.setText(songName);
        editSongBtn = findViewById(R.id.idBtnEditSong);
        backToHomeBtn = findViewById(R.id.idBtnBackToHome);

        // Initializing our all variables
        versionModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(ViewSongAndVersionsActivity.this);

        // Get id from SQLite DB
        Integer songId = dbHandler.getSongId(songName);

        // getting our song array list from db handler class
        versionModalArrayList = dbHandler.readVersions(songId);

        // Passing our version array list to our adapter class
        versionRVAdapter = new VersionRVAdapter(versionModalArrayList, ViewSongAndVersionsActivity.this);
        versionsRV = findViewById(R.id.idRVVersions);

        // Setting layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewSongAndVersionsActivity.this, RecyclerView.VERTICAL, false);
        versionsRV.setLayoutManager(linearLayoutManager);

        // Setting adapter to recycler view
        versionsRV.setAdapter(versionRVAdapter);

        // Get shared preferences
        songData = this.getSharedPreferences(StringHelper.SongData_SharedPreferences, Context.MODE_PRIVATE);

        // Back to home
        editSongBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Save name in SharedPreferences
                SharedPreferences.Editor editor = songData.edit();
                editor.putString(StringHelper.SongData_Preference_Name, songName);
                editor.apply();

                // Opening a new activity via a intent
                Intent i = new Intent(ViewSongAndVersionsActivity.this, EditSongActivity.class);
                startActivity(i);
            }
        });

        // Back to home
        backToHomeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(ViewSongAndVersionsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
