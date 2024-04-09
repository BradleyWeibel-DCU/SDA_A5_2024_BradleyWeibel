package com.example.sda_a5_2024_bradleyweibel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddVersionActivity extends AppCompatActivity
{
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private Button createBtn, backToHomeBtn;
    private DBHandler dbHandler;
    private SharedPreferences songData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_version);

        // Initializing all our variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameEdt = findViewById(R.id.idEdtVersionName);
        versionDescriptionEdt = findViewById(R.id.idEdtVersionDescription);
        versionLyricsEdt = findViewById(R.id.idEdtVersionLyrics);
        createBtn = findViewById(R.id.idBtnAddVersion);
        backToHomeBtn = findViewById(R.id.idBtnBackToHome);

        // Creating a new dbhandler class and passing our context to it
        dbHandler = new DBHandler(AddVersionActivity.this);

        // Get shared preferences
        songData = this.getSharedPreferences(StringHelper.SongData_SharedPreferences, Context.MODE_PRIVATE);
        // Set song name in UI
        songNameTxt.setText(songData.getString(StringHelper.SongData_Preference_Name, ""));

        // Add on click listener for add song and version button.
        createBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                String songName = songNameTxt.getText().toString().trim();
                String versionName = versionNameEdt.getText().toString().trim();
                String versionDescription = versionDescriptionEdt.getText().toString().trim();
                String versionLyrics = versionLyricsEdt.getText().toString().trim();

                // Validating if needed text fields are empty or not
                if (songName.isEmpty() || versionName.isEmpty()) {
                    StringHelper.showToast("Please enter all names", AddVersionActivity.this);
                    return;
                }

                String creationDate = StringHelper.getFormattedDate();

                // Calling a method to add new song to sqlite data and pass all our values to it
                Integer songId = dbHandler.addNewSong(songName, creationDate, creationDate);

                // Calling a method to add new version to sqlite data and pass all our values to it
                dbHandler.addNewVersion(versionName, songId, versionDescription, versionLyrics, creationDate, creationDate);

                // after adding the data we are displaying a toast message
                StringHelper.showToast("Song and version added", AddVersionActivity.this);

                // Returning to home page
                Intent i = new Intent(AddVersionActivity.this, MainActivity.class);
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
                Intent i = new Intent(AddVersionActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
