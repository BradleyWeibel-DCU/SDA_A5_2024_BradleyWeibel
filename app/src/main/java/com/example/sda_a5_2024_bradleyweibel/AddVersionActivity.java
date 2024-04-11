package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddVersionActivity extends AppCompatActivity
{
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton createBtn, backToSongBtn;
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
        String songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        songNameTxt.setText(songName);
        versionNameEdt.requestFocus();

        final Integer[] songId = {dbHandler.getSongId(songName)};
        final Integer[] versionId = {0};

        // Add on click listener for add song and version button.
        createBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                String versionName = versionNameEdt.getText().toString().trim();
                String versionDescription = versionDescriptionEdt.getText().toString().trim();
                String versionLyrics = versionLyricsEdt.getText().toString().trim();

                // Validating if needed text fields are empty or not
                if (versionName.isEmpty()) {
                    StringHelper.showToast(getString(R.string.toastr_missing_version_name), AddVersionActivity.this);
                    return;
                }

                String creationDate = StringHelper.getFormattedDate();

                if (songId[0].equals(0))
                {
                    // Song doesn't exist yet
                    // Add new song to DB
                    songId[0] = dbHandler.addNewSong(songName, creationDate, creationDate);
                    // Add new version of song to DB
                    versionId[0] = dbHandler.addNewVersion(versionName, songId[0], versionDescription, versionLyrics, creationDate, creationDate);

                    StringHelper.showToast(getString(R.string.toastr_song_and_version_added), AddVersionActivity.this);
                }
                else
                {
                    // Song already exists, add a new version for this song
                    // Version name must be unique in the context of this song
                    if (!dbHandler.isVersionNameUnique(songId[0], versionName))
                    {
                        StringHelper.showToast(getString(R.string.toastr_unique_version_name), AddVersionActivity.this);
                        return;
                    }
                    // Add new version of song to DB
                    versionId[0] = dbHandler.addNewVersion(versionName, songId[0], versionDescription, versionLyrics, creationDate, creationDate);
                }


                // Go to view version screen
                Intent i = new Intent(AddVersionActivity.this, ViewVersionActivity.class);
                // Pass data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId[0]);
                startActivity(i);
            }
        });

        // Back to song button is clicked
        backToSongBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i;
                if (songId[0].equals(0))
                {
                    // Song has not been created yet, go back to create song screen
                    // Opening a new activity via an intent
                    i = new Intent(AddVersionActivity.this, AddSongActivity.class);
                }
                else
                {
                    // Song has been created already, go back to 'song and versions' screen
                    // Opening a new activity via an intent
                    i = new Intent(AddVersionActivity.this, ViewSongAndVersionsActivity.class);
                }
                // Passing the song name
                i.putExtra(StringHelper.SongData_Intent_Name, songNameTxt.getText().toString());
                startActivity(i);
            }
        });
    }
}
