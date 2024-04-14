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
    private Integer versionId;
    private String originalVersionName, songName;
    private EditText versionNameEdt;
    private TextView songNameTxt;
    private VersionModal versionData;
    private FloatingActionButton nextBtn, deleteBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_version);

        // Initializing all our variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameEdt = findViewById(R.id.idEdtVersionName);
        nextBtn = findViewById(R.id.idBtnNext);
        deleteBtn = findViewById(R.id.idBtnDelete);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        // Get version ID from intent
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);

        // Initiate DB handler
        dbHandler = new DBHandler(EditVersionActivity.this);
        // Get version data
        versionData = dbHandler.getSongVersion(versionId);
        songName = dbHandler.getSongName(versionData.getVersionSongId());
        originalVersionName = versionData.getVersionName();

        // Set original song name in UI
        songNameTxt.setText(songName);
        versionNameEdt.setText(originalVersionName);
        versionNameEdt.requestFocus();

        // Add on click listener for next button
        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get version name from edit text field
                String newVersionName = versionNameEdt.getText().toString().trim();

                // Validate if the text field is empty or not.
                if (newVersionName.isEmpty())
                {
                    StringHelper.showToast(getString(R.string.toastr_missing_version_name), EditVersionActivity.this);
                    return;
                }
                else if (!newVersionName.equals(originalVersionName))
                    if (!dbHandler.isVersionNameUnique(versionData.getVersionSongId(), newVersionName))
                    {
                        StringHelper.showToast(getString(R.string.toastr_unique_version_name), EditVersionActivity.this);
                        return;
                    }

                // Go to 'Edit Version Data' page
                Intent i = new Intent(EditVersionActivity.this, EditVersionDataActivity.class);
                // Passing the new song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                i.putExtra(StringHelper.VersionData_Intent_Name, newVersionName);
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
                Intent i = new Intent(EditVersionActivity.this, DeleteSongOrVersionActivity.class);
                // Passing version data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                i.putExtra(StringHelper.VersionData_Intent_Name, originalVersionName);
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
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
                Intent i = new Intent(EditVersionActivity.this, ViewSongAndVersionsActivity.class);
                // Passing original song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });
    }
}
