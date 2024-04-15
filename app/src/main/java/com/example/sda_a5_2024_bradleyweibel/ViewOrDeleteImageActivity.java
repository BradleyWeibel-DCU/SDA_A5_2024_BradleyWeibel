package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;

public class ViewOrDeleteImageActivity extends AppCompatActivity
{
    // UI elements
    private ImageView imageViewer;
    private TextView songNameTxt;
    private FloatingActionButton backBtn, deleteBtn;

    // General elements
    private Integer versionId;
    private Boolean wasPreviousScreenAddVersionData;
    private String songName, versionName, versionDescription, versionLyrics, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_or_delete_image);

        // Attaching local variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        imageViewer = findViewById(R.id.idImgImageViewer);
        deleteBtn = findViewById(R.id.idBtnDelete);
        backBtn = findViewById(R.id.idBtnBack);

        // Get values from intent
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        versionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        wasPreviousScreenAddVersionData = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_Add_Screen, false);
        imagePath = getIntent().getStringExtra(StringHelper.ImageData_Intent_Path);

        // Populate UI elements
        songNameTxt.setText(songName);
        File imageFile = new File(imagePath);
        imageViewer.setImageURI(Uri.fromFile(imageFile));
        // Don't show the delete button if the previous screen was ViewVersionData
        if (versionDescription == null)
            deleteBtn.setVisibility(View.INVISIBLE);

        // Delete button is clicked
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Delete image
                imageFile.delete();

                Intent i;
                if (wasPreviousScreenAddVersionData)
                {
                    // Previous screen was AddVersionData
                    i = new Intent(ViewOrDeleteImageActivity.this, AddVersionActivity.class);
                }
                else
                {
                    // Previous screen was EditVersion
                    i = new Intent(ViewOrDeleteImageActivity.this, EditVersionActivity.class);
                }
                i = populateIntentData(i);
                startActivity(i);
            }
        });

        // Back button is clicked
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i;
                if (versionDescription == null)
                {
                    // Previous screen was ViewVersionData
                    i = new Intent(ViewOrDeleteImageActivity.this, ViewVersionDataActivity.class);
                    i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                }
                else if (wasPreviousScreenAddVersionData)
                {
                    // Previous screen was AddVersionData
                    i = new Intent(ViewOrDeleteImageActivity.this, AddVersionActivity.class);
                    i = populateIntentData(i);
                }
                else
                {
                    // Previous screen was EditVersion
                    i = new Intent(ViewOrDeleteImageActivity.this, EditVersionActivity.class);
                    i = populateIntentData(i);
                }
                startActivity(i);
            }
        });
    }

    private Intent populateIntentData(Intent i)
    {
        if (wasPreviousScreenAddVersionData)
        {
            i.putExtra(StringHelper.SongData_Intent_Name, songName);
            i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
            i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
            i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
            i.putExtra(StringHelper.VersionData_Intent_View_Screen, true);
        }
        else
        {
            i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
            i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
            i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
            i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
            i.putExtra(StringHelper.VersionData_Intent_View_Screen, true);
        }
        return i;
    }
}