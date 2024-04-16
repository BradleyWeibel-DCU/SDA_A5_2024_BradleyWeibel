package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;

public class ViewVersionActivity extends AppCompatActivity
{
    private TextView songNameTxt, versionNameTxt, versionDescriptionTxt, versionLyricsTxt;
    private FloatingActionButton editVersionBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private LinearLayout imageContainerLyt, videoContainerLyt;

    // General variables
    private Boolean imagesPresent, videosPresent;
    private Integer versionId;
    private String songName, versionName, versionDescription, versionLyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_version);

        // Getting version data from the adapter class
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);

        // Attaching variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameTxt = findViewById(R.id.idTxtVersionName);
        versionDescriptionTxt = findViewById(R.id.idTxtVersionDescription);
        versionLyricsTxt = findViewById(R.id.idTxtVersionLyrics);
        imageContainerLyt = findViewById(R.id.idLytImageContainer);
        videoContainerLyt = findViewById(R.id.idLytVideoContainer);
        editVersionBtn = findViewById(R.id.idBtnEditVersion);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        // Initializing db Handler
        dbHandler = new DBHandler(ViewVersionActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        // Get values
        songName = dbHandler.getSongName(versionData.getVersionSongId());
        versionName = versionData.getVersionName();
        versionDescription = versionData.getVersionDescription();
        versionLyrics = versionData.getVersionLyrics();

        // Image handling
        imagesPresent = false;
        getVersionImages();

        // Video handling
        videosPresent = false;
        getVersionVideos();

        // UI elements handling
        songNameTxt.setText(songName);
        versionNameTxt.setText(versionName);
        if (versionDescription.equals(""))
        {
            // No description text saved, remove elements
            TextView header = findViewById(R.id.idTxtDescriptionHeader);
            ((ViewGroup) header.getParent()).removeView(header);
            ((ViewGroup) versionDescriptionTxt.getParent()).removeView(versionDescriptionTxt);
        }
        else
            versionDescriptionTxt.setText(versionDescription);
        if (versionLyrics.equals(""))
        {
            // No lyric text saved, remove elements
            TextView header = findViewById(R.id.idTxtLyricsHeader);
            ((ViewGroup) header.getParent()).removeView(header);
            ((ViewGroup) versionLyricsTxt.getParent()).removeView(versionLyricsTxt);
        }
        else
            versionLyricsTxt.setText(versionLyrics);
        if (!imagesPresent)
        {
            // No images saved, remove elements
            TextView header = findViewById(R.id.idTxtImagesHeader);
            ((ViewGroup) header.getParent()).removeView(header);
            ((ViewGroup) imageContainerLyt.getParent()).removeView(imageContainerLyt);
        }
        if (!videosPresent)
        {
            // No videos saved, remove elements
            TextView header = findViewById(R.id.idTxtVideosHeader);
            ((ViewGroup) header.getParent()).removeView(header);
            ((ViewGroup) videoContainerLyt.getParent()).removeView(videoContainerLyt);
        }

        // Edit the version button is clicked
        editVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent
                Intent i = new Intent(ViewVersionActivity.this, EditVersionActivity.class);
                // Passing version id through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                startActivity(i);
            }
        });

        // Back to song and versions screen
        backToSongAndVersionsBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(ViewVersionActivity.this, ViewSongAndVersionsActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });
    }

    // --------------------------------------------- Image handling
    // Get list of already created images for this version
    private void getVersionImages()
    {
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());

                    // Create new ImageView
                    ImageView imageView = new ImageView(ViewVersionActivity.this);
                    // Set the parameters
                    int dimensions = imageViewDPSizeInPX();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
                    // Set the margin in linearlayout
                    params.setMargins(0, 10, 0, 10);
                    imageView.setLayoutParams(params);
                    imageView.setImageURI(Uri.fromFile(imageFile));
                    imageView.setTag(currentFile.getPath());
                    imageView.setOnClickListener(v -> { viewImage(v.getTag().toString()); });
                    // Insert ImageView into UI
                    imageContainerLyt.addView(imageView);
                    imagesPresent = true;
                }
            }
        }
    }
    private void viewImage(String imagePath)
    {
        // Song name
        Intent i = new Intent(ViewVersionActivity.this, ViewOrDeleteImageActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.ImageData_Intent_Path, imagePath);
        startActivity(i);
    }

    // --------------------------------------------- Video handling
    // Get list of already created videos for this version
    private void getVersionVideos()
    {
        File file = new File(StringHelper.Video_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Video_Folder_Path + "/";
            String videoPrefix = StringHelper.Video_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(videoPrefix))
                {
                    // Video belonging to this song and version found
                    File videoFile = new File(currentFile.getPath());

                    // The following code was developed with the aid of ChatGPT
                    // <<<<<<< Start of Chat GPT aided code >>>>>>>>
                    // Create a MediaMetadataRetriever
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    try
                    {
                        // Set the data source to the video location
                        retriever.setDataSource(this, Uri.fromFile(videoFile));
                        // Extract a frame at the specified time (e.g., 1 second)
                        Bitmap thumbnail = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        // Create a new ImageView
                        ImageView imageView = new ImageView(this);
                        imageView.setTag(currentFile.getPath());
                        imageView.setOnClickListener(v -> { viewVideo(v.getTag().toString()); });
                        // Set the thumbnail as the image source
                        imageView.setImageBitmap(thumbnail);
                        // Limit the maximum height of the ImageView to 130dp
                        int maxHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
                        // Set layout parameters
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, maxHeightInPx);
                        layoutParams.setMargins(0, 10, 0, 10); // Set margins (left, top, right, bottom)
                        imageView.setLayoutParams(layoutParams);
                        // Add the ImageView to your layout
                        videoContainerLyt.addView(imageView);
                        // <<<<<<< End of Chat GPT code >>>>>>>>
                    }
                    catch (Exception e) {}
                    finally
                    {
                        // Release the MediaMetadataRetriever
                        try
                        {
                            retriever.release();
                        }
                        catch (IOException e) {}
                    }
                    videosPresent = true;
                }
            }
        }
    }
    private void viewVideo(String videoPath)
    {
        // Song name
        Intent i = new Intent(ViewVersionActivity.this, ViewOrDeleteVideoActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VideoData_Intent_Path, videoPath);
        startActivity(i);
    }

    // ---------------------------------------------- Helpers
    private int imageViewDPSizeInPX()
    {
        float dip = 130f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        int result = Math.round(px);
        return result;
    }
}