package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    // UI elements
    private TextView songNameTxt, versionNameTxt, versionDescriptionTxt, versionLyricsTxt;
    private FloatingActionButton editVersionBtn, backToSongAndVersionsBtn;
    private LinearLayout imageContainerLyt, videoContainerLyt, recordingContainerLyt;

    // General variables
    private Boolean imagesPresent, videosPresent, recordingsPresent;
    private Integer versionId, recordingsCounter;
    private String songName, versionName, versionDescription, versionLyrics;
    private DBHandler dbHandler;
    private VersionModal versionData;

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
        recordingContainerLyt = findViewById(R.id.idLytRecordingsContainer);
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

        // Recordings handling
        recordingsCounter = 1;
        recordingsPresent = false;
        getVersionRecordings();

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
        if (!recordingsPresent)
        {
            // No recordings saved, remove elements
            TextView header = findViewById(R.id.idTxtRecordingsHeader);
            ((ViewGroup) header.getParent()).removeView(header);
            ((ViewGroup) recordingContainerLyt.getParent()).removeView(recordingContainerLyt);
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

                    // The following code was developed with the aid of ChatGPT after constant failed attempts
                    // <<<<<<< Start of Chat GPT aided code >>>>>>>>
                    // Calculate the dimensions for scaling down the bitmap
                    int targetWidth = NumberHelper.imageViewDPSizeInPX(getResources());
                    int targetHeight = NumberHelper.imageViewDPSizeInPX(getResources());
                    // Decode the bitmap from the file, scaled down to the target dimensions
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                    int imageWidth = options.outWidth;
                    int imageHeight = options.outHeight;
                    int scaleFactor = Math.min(imageWidth / targetWidth, imageHeight / targetHeight);
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scaleFactor;
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
                    // <<<<<<< End of Chat GPT code >>>>>>>>

                    // Create new ImageView
                    ImageView imageView = new ImageView(ViewVersionActivity.this);
                    // Set the parameters
                    int dimensions = NumberHelper.imageViewDPSizeInPX(getResources());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
                    // Set the margin in linearlayout
                    params.setMargins(NumberHelper.Image_Margin_Left, NumberHelper.Image_Margin_Top, NumberHelper.Video_Margin_Right, NumberHelper.Video_Margin_Bottom);
                    imageView.setLayoutParams(params);
                    // Insert the bitmap rather than the full image
                    imageView.setImageBitmap(bitmap);
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
                        Bitmap thumbnail = retriever.getFrameAtTime(NumberHelper.Video_Get_Frame_At_1_Second, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        // Create a new ImageView
                        ImageView imageView = new ImageView(this);
                        imageView.setTag(currentFile.getPath());
                        imageView.setOnClickListener(v -> { viewVideo(v.getTag().toString()); });
                        // Set the thumbnail as the image source
                        imageView.setImageBitmap(thumbnail);
                        // Limit the maximum height of the ImageView to 130dp
                        int maxHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, NumberHelper.Video_Thumbnail_Height, getResources().getDisplayMetrics());
                        // Set layout parameters
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, maxHeightInPx);
                        // Set margins (left, top, right, bottom)
                        layoutParams.setMargins(NumberHelper.Video_Margin_Left, NumberHelper.Video_Margin_Top, NumberHelper.Video_Margin_Right, NumberHelper.Video_Margin_Bottom);
                        imageView.setLayoutParams(layoutParams);
                        // Add the ImageView to your layout
                        videoContainerLyt.addView(imageView);
                        // <<<<<<< End of Chat GPT aided code >>>>>>>>
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
        Intent i = new Intent(ViewVersionActivity.this, ViewOrDeleteVideoActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VideoData_Intent_Path, videoPath);
        startActivity(i);
    }

    // --------------------------------------------- Audio handling
    // Get list of already created recordings for this version
    private void getVersionRecordings()
    {
        File file = new File(StringHelper.Audio_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Audio_Folder_Path + "/";
            String audioPrefix = StringHelper.Audio_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(audioPrefix))
                {
                    // Audio belonging to this song and version found
                    // Create new ImageView
                    ImageView imageView = new ImageView(ViewVersionActivity.this);
                    // Set the parameters
                    int dimensions = NumberHelper.imageViewDPSizeInPX(getResources());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
                    // Set the margin in linearlayout
                    params.setMargins(NumberHelper.Image_Margin_Left, NumberHelper.Image_Margin_Top, NumberHelper.Image_Margin_Right, NumberHelper.Image_Margin_Bottom);
                    imageView.setLayoutParams(params);
                    // Insert the bitmap rather than the full image
                    imageView.setImageDrawable(getDrawable(NumberHelper.isNumberEven(recordingsCounter) ? R.drawable.audio_photo_1 : R.drawable.audio_photo_2));
                    imageView.setTag(currentFile.getPath());
                    imageView.setOnClickListener(v -> { viewAudioRecording(v.getTag().toString()); });
                    // Insert ImageView into UI
                    recordingContainerLyt.addView(imageView);
                    recordingsPresent = true;
                    recordingsCounter += 1;
                }
            }
        }
    }
    private void viewAudioRecording(String recordingPath)
    {
        Intent i = new Intent(ViewVersionActivity.this, RecordOrPlayAudioActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.AudioData_Intent_Path, recordingPath);
        startActivity(i);
    }
}
