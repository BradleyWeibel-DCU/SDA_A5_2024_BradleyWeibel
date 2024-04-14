package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;

public class EditSongActivity extends AppCompatActivity
{
    private Integer songId;
    private String originalSongName, newSongName;
    private EditText songNameEdt;
    private FloatingActionButton saveBtn, deleteBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_song);

        // Initializing all our variables
        songNameEdt = findViewById(R.id.idEdtSongName);
        saveBtn = findViewById(R.id.idBtnSave);
        deleteBtn = findViewById(R.id.idBtnDelete);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        // Get original song name from intent
        originalSongName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        // Set original song name in UI
        songNameEdt.setText(originalSongName);
        songNameEdt.requestFocus();

        // Initiate DB handler
        dbHandler = new DBHandler(EditSongActivity.this);
        songId = dbHandler.getSongId(originalSongName);

        // Add on click listener for the save song button
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get song name from edit text field
                newSongName = songNameEdt.getText().toString().trim();

                // validating if the text fields are empty or not.
                if (newSongName.isEmpty())
                {
                    StringHelper.showToast(getString(R.string.toastr_missing_song_name), EditSongActivity.this);
                    return;
                }
                else if (!dbHandler.isSongNameUnique(newSongName))
                {
                    StringHelper.showToast(getString(R.string.toastr_unique_song_name), EditSongActivity.this);
                    return;
                }

                // Save changes in SQLite DB
                String modificationDate = StringHelper.getFormattedDate();
                // Update song with new name
                dbHandler.updateSong(songId, newSongName, modificationDate);

                // TODO: rename all images, videos and audio clips with new song name
                renameImages();

                // Go to 'View Song and Versions' page
                Intent i = new Intent(EditSongActivity.this, ViewSongAndVersionsActivity.class);
                // Passing the new song name
                i.putExtra(StringHelper.SongData_Intent_Name, newSongName);
                startActivity(i);
            }
        });

        // Delete song button clicked
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent
                Intent i = new Intent(EditSongActivity.this, DeleteSongOrVersionActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_ID, songId);
                i.putExtra(StringHelper.SongData_Intent_Name, originalSongName);
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

    private void renameImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            String imagePrefix = StringHelper.Image_Prefix + originalSongName + "_";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    String newFileName = currentFile.getPath();
                    newFileName = newFileName.replace(imagePrefix, StringHelper.Image_Prefix + newSongName + "_");
                    // Rename file with new version name
                    File newNameImageFile = new File(newFileName);
                    imageFile.renameTo(newNameImageFile);
                }
            }
        }
    }
}
