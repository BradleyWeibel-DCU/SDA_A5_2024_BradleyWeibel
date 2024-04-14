package com.example.sda_a5_2024_bradleyweibel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;

public class AddVersionDataActivity extends AppCompatActivity
{
    // UI elements
    private TextView songNameTxt, versionNameTxt;
    private EditText versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton createBtn, backToVersionBtn;
    private LinearLayout imageContainerLyt;
    private ImageButton galleryImageBtn, newImageBtn, galleryVideoBtn, newVideoBtn, galleryAudioBtn, newAudioBtn;

    // General variables
    private Integer imageCounter;
    private DBHandler dbHandler;
    private Boolean wasPreviousScreenImageViewer;
    private String songName, versionName, currentPhotoPath, versionDescription, versionLyrics;

    // Static keys
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_VIDEO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_version_data);

        // Mapping local variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameTxt = findViewById(R.id.idTxtVersionName);
        versionDescriptionEdt = findViewById(R.id.idEdtVersionDescription);
        versionLyricsEdt = findViewById(R.id.idEdtVersionLyrics);
        imageContainerLyt = findViewById(R.id.imageContainer);
        galleryImageBtn = findViewById(R.id.idAddGalleryImageBtn);
        newImageBtn = findViewById(R.id.idAddNewImageBtn);
        galleryVideoBtn = findViewById(R.id.idAddGalleryVideoBtn);
        newVideoBtn = findViewById(R.id.idAddNewVideoBtn);
        galleryAudioBtn = findViewById(R.id.idAddGalleryAudioBtn);
        newAudioBtn = findViewById(R.id.idAddNewAudioBtn);
        createBtn = findViewById(R.id.idBtnAddVersion);
        backToVersionBtn = findViewById(R.id.idBtnBackToAddVersion);

        // Initiate counter used for image handling
        imageCounter = 1;

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(AddVersionDataActivity.this);

        // Get all intent data
        songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        versionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        // If returning from Image viewer, more intent data is passed
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        wasPreviousScreenImageViewer = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_View_Screen, false);

        // Populate UI elements
        songNameTxt.setText(songName);
        versionNameTxt.setText(versionName);
        if (wasPreviousScreenImageViewer)
        {
            versionDescriptionEdt.setText(versionDescription);
            versionLyricsEdt.setText(versionLyrics);
            getVersionImages();
        }

        final Integer[] songId = {dbHandler.getSongId(songName)};
        final Integer[] versionId = {0};

        // Add on click listener for add song and version button
        createBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                versionDescription = versionDescriptionEdt.getText().toString().trim();
                versionLyrics = versionLyricsEdt.getText().toString().trim();

                String creationDate = StringHelper.getFormattedDate();

                if (songId[0].equals(0))
                {
                    // Song doesn't exist yet
                    // Add new song to DB
                    songId[0] = dbHandler.addNewSong(songName, creationDate, creationDate);
                    // Add new version of song to DB
                    versionId[0] = dbHandler.addNewVersion(versionName, songId[0], versionDescription, versionLyrics, creationDate, creationDate);

                    // TODO: add version name to newly added images for this song

                    StringHelper.showToast(getString(R.string.toastr_song_and_version_added), AddVersionDataActivity.this);
                }
                else
                {
                    // Song already exists, add a new version for this song
                    // Add new version of song to DB
                    versionId[0] = dbHandler.addNewVersion(versionName, songId[0], versionDescription, versionLyrics, creationDate, creationDate);

                    // TODO: check if version name has changed? add version name to newly added images for this song
                }

                // Go to view version screen
                Intent i = new Intent(AddVersionDataActivity.this, ViewVersionDataActivity.class);
                // Pass data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId[0]);
                startActivity(i);
            }
        });

        // Back to version button is clicked
        backToVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: remove any newly created images, videos or audio clips
                removeAllNewImages();
                // Opening a new activity via an intent
                Intent i = new Intent(AddVersionDataActivity.this, AddVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
                startActivity(i);
            }
        });

        // --------------------------- Image handling
        // User clicks button wanting to take a new photo
        newImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isCameraPermissionGranted() && isWriteExternalPermissionGranted() && isReadExternalPermissionGranted())
                    openCamera();
                else
                {
                    if (!isCameraPermissionGranted())
                        askCameraPermission();
                    else if (!isWriteExternalPermissionGranted())
                        askWriteStoragePermission();
                    else if (!isReadExternalPermissionGranted())
                        askReadStoragePermission();
                }
            }
        });
    }

    // --------------------------------------------- Creating new images in UI
    // Permission granted, proceed with image capture (and later saving)
    private void openCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {}

            // If file was successfully created
            if (photoFile != null)
            {
                // TODO: move authority to StringHelper
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.sda_a5_2024_bradleyweibel", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Create image and save in phones storage
    private File createImageFile() throws IOException
    {
        String imageFileName = StringHelper.Image_Prefix + songName + "_" + versionName + "_" + imageCounter;
        // Location: Phone > Android > data > com.example.sda_a5_2024_bradleyweibel > files > Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // After a photo/video has been taken and the tick clicked in UI
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
        {
            // Image was successfully taken with the camera and created
            File imageFile = new File(currentPhotoPath);

            // Create new ImageView
            ImageView imageView = new ImageView(AddVersionDataActivity.this);
            // Set the parameters
            int dimensions = imageViewDPSizeInPX();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
            // Set the margin in linearlayout
            params.setMargins(0, 10, 0, 10);
            imageView.setLayoutParams(params);
            imageView.setImageURI(Uri.fromFile(imageFile));
            imageView.setTag(currentPhotoPath);
            imageView.setOnClickListener(v -> { viewImage(v.getTag().toString()); });
            // Insert ImageView into UI
            imageContainerLyt.addView(imageView);

            // Move to next image
            imageCounter+=1;
        }
        else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK)
        {
            // Video was successfully taken with the camera
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

    private void removeAllNewImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files) {
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

    private void viewImage(String imagePath)
    {
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(AddVersionDataActivity.this, ViewOrDeleteImageActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.ImageData_Intent_Path, imagePath);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, true);
        startActivity(i);
    }


    // --------------------------------------------- Permission handling
    // Check if permissions are already granted
    private Boolean isCameraPermissionGranted() { return ContextCompat.checkSelfPermission(AddVersionDataActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isWriteExternalPermissionGranted() { return ContextCompat.checkSelfPermission(AddVersionDataActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isReadExternalPermissionGranted() { return ContextCompat.checkSelfPermission(AddVersionDataActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }

    // Asking for permission
    private void askCameraPermission() { ActivityCompat.requestPermissions(AddVersionDataActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CODE); }
    private void askWriteStoragePermission() { ActivityCompat.requestPermissions(AddVersionDataActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE); }
    private void askReadStoragePermission() { ActivityCompat.requestPermissions(AddVersionDataActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE); }

    // Trigger on response to permission prompt from User
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (isCameraPermissionGranted() && isWriteExternalPermissionGranted() && isReadExternalPermissionGranted())
                openCamera();
            else if (!isCameraPermissionGranted())
                askCameraPermission();
            else if (!isWriteExternalPermissionGranted())
                askWriteStoragePermission();
            else if (!isReadExternalPermissionGranted())
                askReadStoragePermission();
        }
        else
        {
            // TODO
            StringHelper.showToast("Please accept all permission prompts to use these features", AddVersionDataActivity.this);
        }
    }






    // Get list of already created images for this version
    private void getVersionImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());

                    // Create new ImageView
                    ImageView imageView = new ImageView(AddVersionDataActivity.this);
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

                    // Move to next image
                    imageCounter+=1;
                }
            }
        }
    }
}
