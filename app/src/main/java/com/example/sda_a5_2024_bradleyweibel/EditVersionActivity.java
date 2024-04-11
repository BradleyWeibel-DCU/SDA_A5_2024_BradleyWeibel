package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditVersionActivity extends AppCompatActivity
{
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton saveBtn, deleteBtn, backToViewVersionBtn;
    private DBHandler dbHandler;
    private VersionModal versionData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_version);

        // Getting song name which we passed in the adapter class
        Integer versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);

        // Initializing all our variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameEdt = findViewById(R.id.idEdtVersionName);
        versionDescriptionEdt = findViewById(R.id.idEdtVersionDescription);
        versionLyricsEdt = findViewById(R.id.idEdtVersionLyrics);
        saveBtn = findViewById(R.id.idBtnSaveVersion);
        deleteBtn = findViewById(R.id.idBtnDeleteVersion);
        backToViewVersionBtn = findViewById(R.id.idBtnBack);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(EditVersionActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);

        String songName = dbHandler.getSongName(versionData.getVersionSongId());
        String versionName = versionData.getVersionName();

        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameEdt.setText(versionName);
        versionDescriptionEdt.setText(versionData.getVersionDescription());
        versionLyricsEdt.setText(versionData.getVersionLyrics());

        // On click listener for save version changes button
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                String originalVersionName = versionData.getVersionName();
                String newVersionName = versionNameEdt.getText().toString().trim();
                String versionDescription = versionDescriptionEdt.getText().toString().trim();
                String versionLyrics = versionLyricsEdt.getText().toString().trim();

                // Validating if version name is empty
                if (newVersionName.isEmpty()) {
                    StringHelper.showToast(getString(R.string.toastr_missing_version_name), EditVersionActivity.this);
                    return;
                }
                else if (!newVersionName.equals(originalVersionName))
                    if (!dbHandler.isVersionNameUnique(versionData.getVersionSongId(), newVersionName))
                    {
                        StringHelper.showToast(getString(R.string.toastr_unique_version_name), EditVersionActivity.this);
                        return;
                    }

                String modificationDate = StringHelper.getFormattedDate();
                // Add new version of song to DB
                dbHandler.updateVersion(versionId, newVersionName, versionDescription, versionLyrics, modificationDate);

                StringHelper.showToast(getString(R.string.toastr_new_version_created), EditVersionActivity.this);

                // Go to view version screen
                Intent i = new Intent(EditVersionActivity.this, ViewVersionActivity.class);
                // Pass data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                startActivity(i);
            }
        });

        // Delete version button is clicked
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via an intent
                Intent i = new Intent(EditVersionActivity.this, DeleteSongOrVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });

        // Back to view version button is clicked
        backToViewVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(EditVersionActivity.this, ViewVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                startActivity(i);
            }
        });
    }
}
