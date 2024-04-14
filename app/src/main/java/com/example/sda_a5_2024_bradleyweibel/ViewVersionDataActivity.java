package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;

public class ViewVersionDataActivity extends AppCompatActivity
{
    private TextView songNameTxt, versionNameTxt, versionDescriptionTxt, versionLyricsTxt;
    private FloatingActionButton editVersionBtn, backToSongAndVersionsBtn;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private LinearLayout imageContainerLyt;

    // General variables
    private Integer imageCounter;

    private String songName, versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_version_data);

        // Getting version data from the adapter class
        Integer versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);

        // Attaching variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameTxt = findViewById(R.id.idTxtVersionName);
        versionDescriptionTxt = findViewById(R.id.idTxtVersionDescription);
        versionLyricsTxt = findViewById(R.id.idTxtVersionLyrics);
        imageContainerLyt = findViewById(R.id.imageContainer);
        editVersionBtn = findViewById(R.id.idBtnEditVersion);
        backToSongAndVersionsBtn = findViewById(R.id.idBtnBackToSongAndVersions);

        imageCounter = 1;

        // Initializing db Handler
        dbHandler = new DBHandler(ViewVersionDataActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        // Get names
        songName = dbHandler.getSongName(versionData.getVersionSongId());
        versionName = versionData.getVersionName();
        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameTxt.setText(versionData.getVersionName());
        versionDescriptionTxt.setText(versionData.getVersionDescription());
        versionLyricsTxt.setText(versionData.getVersionLyrics());

        getImages();

        // Edit the version button is clicked
        editVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent
                Intent i = new Intent(ViewVersionDataActivity.this, EditVersionActivity.class);
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
                Intent i = new Intent(ViewVersionDataActivity.this, ViewSongAndVersionsActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });
    }

    //
    private void getImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith("IMG_" + songName + "_" + versionName + "_"))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());

                    // Create new ImageView
                    ImageView imageView = new ImageView(ViewVersionDataActivity.this);
                    // Set the parameters
                    int dimensions = imageViewDPSizeInPX();
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
                    // Set the margin in linearlayout
                    params.setMargins(0, 10, 0, 10);
                    imageView.setLayoutParams(params);
                    imageView.setImageURI(Uri.fromFile(imageFile));
                    //imageView.setImageBitmap(cameraImage);

                    // Create ImageView id
                    String selectedImageDisplayID =  "idImageView" + imageCounter.toString();
                    int resourceId = getResources().getIdentifier(selectedImageDisplayID,"id", getPackageName());
                    imageView.setId(resourceId);
                    // Insert ImageView into UI
                    imageContainerLyt.addView(imageView);

                    // Move to next image
                    imageCounter+=1;
                }
            }
        }
    }

    private int imageViewDPSizeInPX()
    {
        float dip = 130f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        int result = Math.round(px);
        return result;
    }
}
