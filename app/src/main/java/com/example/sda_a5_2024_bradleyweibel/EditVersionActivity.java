package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EditVersionActivity extends AppCompatActivity
{
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private Button saveBtn, deleteVersionBtn, backToViewVersionBtn;
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
        backToViewVersionBtn = findViewById(R.id.idBtnBack);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(EditVersionActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        // Get song name
        String songName = dbHandler.getSongName(versionData.getVersionSongId());
        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameEdt.setText(versionData.getVersionName());
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
                    StringHelper.showToast("Please enter a version name", EditVersionActivity.this);
                    return;
                }
                else if (!newVersionName.equals(originalVersionName))
                    if (!dbHandler.isVersionNameUnique(versionData.getVersionSongId(), newVersionName))
                    {
                        StringHelper.showToast("Version name must be unique for this song", EditVersionActivity.this);
                        return;
                    }

                String modificationDate = StringHelper.getFormattedDate();
                // Add new version of song to DB
                dbHandler.updateVersion(versionId, newVersionName, versionDescription, versionLyrics, modificationDate);

                StringHelper.showToast("Version update successful", EditVersionActivity.this);

                // Go to view version screen
                Intent i = new Intent(EditVersionActivity.this, ViewVersionActivity.class);
                // Pass data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
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
