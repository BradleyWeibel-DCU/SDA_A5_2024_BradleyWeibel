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
    private EditText versionNameEdt;
    private FloatingActionButton nextBtn, backToSongBtn;
    private DBHandler dbHandler;
    private String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_version);

        // Mapping UI elements to local variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameEdt = findViewById(R.id.idEdtVersionName);
        nextBtn = findViewById(R.id.idBtnNext);
        backToSongBtn = findViewById(R.id.idBtnBackToAddSong);

        // Initiate DB handler
        dbHandler = new DBHandler(AddVersionActivity.this);

        // Get song name from intent
        songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        songNameTxt.setText(songName);

        final Integer[] songId = {dbHandler.getSongId(songName)};

        // On click listener for the next screen button
        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get version name from edit text field
                String versionName = versionNameEdt.getText().toString().trim();

                // Validate if version name field is populated
                if (versionName.isEmpty()) {
                    StringHelper.showToast(getString(R.string.toastr_missing_version_name), AddVersionActivity.this);
                    return;
                }
                else if (!songId[0].equals(0))
                {
                    // Song already exists, user is planning to add a new version for this song
                    // Version name must be unique in the context of this song
                    if (!dbHandler.isVersionNameUnique(songId[0], versionName))
                    {
                        StringHelper.showToast(getString(R.string.toastr_unique_version_name), AddVersionActivity.this);
                        return;
                    }
                }

                // Go to 'Add Version Data' page
                Intent i = new Intent(AddVersionActivity.this, AddVersionDataActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
                startActivity(i);
            }
        });

        // Back to previous page
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
