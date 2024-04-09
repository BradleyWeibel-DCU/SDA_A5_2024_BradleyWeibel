package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditSongActivity extends AppCompatActivity
{
    private String originalSongName;
    private EditText songNameEdt;
    private Button saveBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        // Initializing all our variables
        songNameEdt = findViewById(R.id.idEdtSongName);
        saveBtn = findViewById(R.id.idBtnSave);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        // Get original song name from intent
        originalSongName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        // Set original song name in UI
        songNameEdt.setText(originalSongName);

        // Initiate DB handler
        dbHandler = new DBHandler(EditSongActivity.this);

        // Add on click listener for the save song button
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get song name from edit text field
                String newSongName = songNameEdt.getText().toString().trim();

                // validating if the text fields are empty or not.
                if (newSongName.isEmpty())
                {
                    // TODO: hard coded string
                    StringHelper.showToast("Please enter a name for the song", EditSongActivity.this);
                    return;
                }
                else if (!dbHandler.isSongNameUnique(newSongName))
                {
                    // TODO: hard coded string
                    StringHelper.showToast("Please enter a unique song name", EditSongActivity.this);
                    return;
                }

                // Save changes in SQLite DB
                // Get song ID for update
                Integer songId = dbHandler.getSongId(originalSongName);
                String todaysDate = StringHelper.getFormattedDate();
                // Update song with new name
                dbHandler.updateSong(songId, newSongName, todaysDate);

                // Go to 'View Song and Versions' page
                Intent i = new Intent(EditSongActivity.this, ViewSongAndVersionsActivity.class);
                // Passing the new song name
                i.putExtra(StringHelper.SongData_Intent_Name, newSongName);
                startActivity(i);
            }
        });

        // Back to view song and versions page
        backToSongAndVersionsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent
                Intent i = new Intent(EditSongActivity.this, ViewSongAndVersionsActivity.class);
                // Passing original song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, originalSongName);
                startActivity(i);
            }
        });
    }
}
