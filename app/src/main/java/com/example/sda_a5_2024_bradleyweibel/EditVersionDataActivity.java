package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EditVersionDataActivity extends AppCompatActivity
{
    private TextView songNameTxt, versionNameTxt;
    private EditText versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton saveBtn, deleteBtn, backToVersionBtn;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private String songName, newVersionName, originalVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_version_data);

        // Getting song name which we passed in the adapter class
        Integer versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        newVersionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);

        // Initializing all our variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameTxt = findViewById(R.id.idTxtVersionName);
        versionDescriptionEdt = findViewById(R.id.idEdtVersionDescription);
        versionLyricsEdt = findViewById(R.id.idEdtVersionLyrics);
        saveBtn = findViewById(R.id.idBtnSaveVersion);
        deleteBtn = findViewById(R.id.idBtnDeleteVersion);
        backToVersionBtn = findViewById(R.id.idBtnBack);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(EditVersionDataActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);

        songName = dbHandler.getSongName(versionData.getVersionSongId());
        originalVersionName = versionData.getVersionName();

        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameTxt.setText(newVersionName);
        versionDescriptionEdt.setText(versionData.getVersionDescription());
        versionLyricsEdt.setText(versionData.getVersionLyrics());

        // On click listener for save version changes button
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                String versionDescription = versionDescriptionEdt.getText().toString().trim();
                String versionLyrics = versionLyricsEdt.getText().toString().trim();

                String modificationDate = StringHelper.getFormattedDate();
                // Update version of song in DB
                dbHandler.updateVersion(versionId, newVersionName, songName, versionDescription, versionLyrics, modificationDate);

                // TODO: image handling, update images with new name. Remove or add as needed

                StringHelper.showToast(getString(R.string.toastr_version_updated), EditVersionDataActivity.this);

                // Go to view version screen
                Intent i = new Intent(EditVersionDataActivity.this, ViewVersionDataActivity.class);
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
                Intent i = new Intent(EditVersionDataActivity.this, DeleteSongOrVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                i.putExtra(StringHelper.VersionData_Intent_Name, originalVersionName);
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });

        // Back to view version button is clicked
        backToVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(EditVersionDataActivity.this, EditVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                startActivity(i);
            }
        });
    }
}
