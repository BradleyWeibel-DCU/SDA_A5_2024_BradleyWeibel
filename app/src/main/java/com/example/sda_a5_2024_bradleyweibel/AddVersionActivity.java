package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
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
    private Button createBtn, backToSongBtn;
    private DBHandler dbHandler;

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
        backToSongBtn = findViewById(R.id.idBtnBackToAddSong);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(AddVersionActivity.this);

        // Set song name in UI from intent
        songNameTxt.setText(getIntent().getStringExtra(StringHelper.SongData_Intent_Name));

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
                // Add new song to DB
                Integer songId = dbHandler.addNewSong(songName, creationDate, creationDate);
                // Add new version of song to DB
                Integer versionId = dbHandler.addNewVersion(versionName, songId, versionDescription, versionLyrics, creationDate, creationDate);

                StringHelper.showToast("Song and version added", AddVersionActivity.this);

                // Go to view version screen
                Intent i = new Intent(AddVersionActivity.this, ViewVersionActivity.class);
                // Pass data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                startActivity(i);
            }
        });

        // Back to song button is clicked
        backToSongBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(AddVersionActivity.this, AddSongActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.SongData_Intent_Name, songNameTxt.getText().toString());
                startActivity(i);
            }
        });
    }
}
