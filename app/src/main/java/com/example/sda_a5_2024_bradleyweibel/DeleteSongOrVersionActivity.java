package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;

public class DeleteSongOrVersionActivity extends AppCompatActivity
{
    private Boolean songWasPassed;
    private Integer songId, versionId;
    private String songName, versionName;
    private TextView songOrVersionTxt, warningTxt;
    private FloatingActionButton deleteBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_song_or_version);
        // Set file paths for saving images, videos, and audio recordings
        StringHelper.setFolderPaths(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)), String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MOVIES)), String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC)));

        // Attaching variables to UI elements
        songOrVersionTxt = findViewById(R.id.idTxtSongOrVersionName);
        warningTxt = findViewById(R.id.idTxtWarning);
        deleteBtn = findViewById(R.id.idBtnDelete);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        // Get possibly supplied values from intent
        songId = getIntent().getIntExtra(StringHelper.SongData_Intent_ID, 0);
        songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        versionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);

        // Are we dealing with a song or a version?
        songWasPassed = versionId.equals(0);

        // Set text in UI depending on what was passed
        String songOrVersionName = songWasPassed ? songName : songName + " - " + versionName;
        songOrVersionTxt.setText(songOrVersionName);
        String warningMessage = songWasPassed ? getString(R.string.delete_song_warning) : getString(R.string.delete_version_warning);
        warningTxt.setText(warningMessage);

        // Add on click listener for the save song button
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Initiate DB handler
                dbHandler = new DBHandler(DeleteSongOrVersionActivity.this);

                Intent i;
                if (songWasPassed)
                {
                    // Delete song and all versions
                    dbHandler.deleteSongAndVersions(songId);
                    // Return to main menu
                    i = new Intent(DeleteSongOrVersionActivity.this, MainActivity.class);

                    // Remove all images, videos and recordings relating to the song
                    removeAllImages(songName, "");
                    removeAllVideos(songName, "");
                    removeAllRecordings(songName, "");
                }
                else
                {
                    // Delete version
                    dbHandler.deleteVersion(versionId);
                    // Return to 'View Song and Versions' page
                    i = new Intent(DeleteSongOrVersionActivity.this, ViewSongAndVersionsActivity.class);
                    // Passing the song name
                    i.putExtra(StringHelper.SongData_Intent_Name, songName);

                    // Remove all images, videos and recordings relating to this version
                    removeAllImages(songName, versionName);
                    removeAllVideos(songName, versionName);
                    removeAllRecordings(songName, versionName);
                }
                startActivity(i);
            }
        });

        // Back to view song and versions page
        backToSongAndVersionsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Initiate DB handler
                dbHandler = new DBHandler(DeleteSongOrVersionActivity.this);

                Intent i;
                if (songWasPassed)
                {
                    // Return to 'Edit Song' page
                    i = new Intent(DeleteSongOrVersionActivity.this, EditSongActivity.class);
                    // Passing the song name
                    i.putExtra(StringHelper.SongData_Intent_Name, songName);
                }
                else
                {
                    // Return to 'Edit Version' page
                    i = new Intent(DeleteSongOrVersionActivity.this, EditVersionActivity.class);
                    // Passing the version id
                    i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                }
                startActivity(i);
            }
        });
    }

    public void removeAllImages(String thisSongName, String thisVersionName)
    {
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            String imagePrefix = StringHelper.Image_Prefix + thisSongName + "_";
            if (!thisVersionName.equals(""))
                imagePrefix = imagePrefix + thisVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    imageFile.delete();
                }
            }
        }
    }
    public void removeAllVideos(String thisSongName, String thisVersionName)
    {
        File file = new File(StringHelper.Video_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Video_Folder_Path + "/";
            String videoPrefix = StringHelper.Video_Prefix + thisSongName + "_";
            if (!thisVersionName.equals(""))
                videoPrefix = videoPrefix + thisVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(videoPrefix))
                {
                    // Video belonging to this song and version found
                    File videoFile = new File(currentFile.getPath());
                    videoFile.delete();
                }
            }
        }
    }
    public void removeAllRecordings(String thisSongName, String thisVersionName)
    {
        File file = new File(StringHelper.Audio_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Audio_Folder_Path + "/";
            String audioPrefix = StringHelper.Audio_Prefix + thisSongName + "_";
            if (!thisVersionName.equals(""))
                audioPrefix = audioPrefix + thisVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(audioPrefix))
                {
                    // Recording belonging to this song and version found
                    File audioFile = new File(currentFile.getPath());
                    audioFile.delete();
                }
            }
        }
    }
}
