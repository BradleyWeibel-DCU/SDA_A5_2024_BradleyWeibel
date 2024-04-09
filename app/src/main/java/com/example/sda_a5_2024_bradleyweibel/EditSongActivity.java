package com.example.sda_a5_2024_bradleyweibel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private SharedPreferences songData;
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

        // Get shared preferences
        songData = this.getSharedPreferences(StringHelper.SongData_SharedPreferences, Context.MODE_PRIVATE);
        // Set song name in UI
        originalSongName = songData.getString(StringHelper.SongData_Preference_Name, "");
        songNameEdt.setText(originalSongName);

        dbHandler = new DBHandler(EditSongActivity.this);

        // below line is to add on click listener for the save song button
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

                // Check name is unique in DB TODO

                // Save changes in SQLite DB
                // Get song ID for update
                Integer songId = dbHandler.getSongId(originalSongName);
                String todaysDate = StringHelper.getFormattedDate();
                // Update song with new name
                dbHandler.updateSong(songId, newSongName, todaysDate);

                // Go to 'View Song and Versions' page
                Intent i = new Intent(EditSongActivity.this, ViewSongAndVersionsActivity.class);
                // Passing all values
                i.putExtra(StringHelper.SongData_Intent_Name, newSongName);
                startActivity(i);
            }
        });

        // Back to home
        backToSongAndVersionsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(EditSongActivity.this, ViewSongAndVersionsActivity.class);
                // Passing all values
                i.putExtra(StringHelper.SongData_Intent_Name, originalSongName);
                startActivity(i);
            }
        });
    }
}
