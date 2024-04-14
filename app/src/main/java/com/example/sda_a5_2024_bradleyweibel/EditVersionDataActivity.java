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
import java.util.ArrayList;

public class EditVersionDataActivity extends AppCompatActivity
{
    // UI elements
    private TextView songNameTxt, versionNameTxt;
    private EditText versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton saveBtn, deleteBtn, backToVersionBtn;
    private ImageButton galleryImageBtn, newImageBtn, galleryVideoBtn, newVideoBtn, galleryAudioBtn, newAudioBtn;
    private LinearLayout imageContainerLyt;

    // General variables
    private Integer versionId, imageCounter;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private Boolean wasPreviousScreenImageViewer;
    private String songName, newVersionName, originalVersionName, versionDescription, versionLyrics, currentPhotoPath;
    private ArrayList<String> listOfNewImageNames;

    // Static keys
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_TAKE_VIDEO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_version_data);

        // Initializing all our variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameTxt = findViewById(R.id.idTxtVersionName);
        versionDescriptionEdt = findViewById(R.id.idEdtVersionDescription);
        versionLyricsEdt = findViewById(R.id.idEdtVersionLyrics);
        imageContainerLyt = findViewById(R.id.imageContainer);
        galleryImageBtn = findViewById(R.id.idAddGalleryImageBtn);
        newImageBtn = findViewById(R.id.idAddNewImageBtn);
        //galleryVideoBtn = findViewById(R.id.idAddGalleryVideoBtn);
        //newVideoBtn = findViewById(R.id.idAddNewVideoBtn);
        //galleryAudioBtn = findViewById(R.id.idAddGalleryAudioBtn);
        //newAudioBtn = findViewById(R.id.idAddNewAudioBtn);
        saveBtn = findViewById(R.id.idBtnSaveVersion);
        deleteBtn = findViewById(R.id.idBtnDeleteVersion);
        backToVersionBtn = findViewById(R.id.idBtnBack);

        listOfNewImageNames = new ArrayList<String>();

        // Getting data which was passed in the adapter class
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        newVersionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        // If returning from Image viewer, more intent data is passed
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        wasPreviousScreenImageViewer = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_View_Screen, false);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(EditVersionDataActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        songName = dbHandler.getSongName(versionData.getVersionSongId());
        originalVersionName = versionData.getVersionName();

        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameTxt.setText(newVersionName);
        if (!wasPreviousScreenImageViewer)
        {
            // Previous screen wa not an image viewer, overwrite intent data with DB data
            versionDescription = versionData.getVersionDescription();
            versionLyrics = versionData.getVersionLyrics();
        }
        versionDescriptionEdt.setText(versionDescription);
        versionLyricsEdt.setText(versionLyrics);

        // Image handling
        imageCounter = 1;
        getVersionImages();

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
                if (!newVersionName.equals(originalVersionName))
                {
                    // Rename files
                    renameImages();
                }

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

        // Back to edit version button is clicked
        backToVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: remove newly added but unsaved images, videos and audio clips
                removeAllNewImages();

                // opening a new activity via a intent.
                Intent i = new Intent(EditVersionDataActivity.this, EditVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
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

    // Get list of already created images for this version
    private void getVersionImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());

                    // Create new ImageView
                    ImageView imageView = new ImageView(EditVersionDataActivity.this);
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
    private int imageViewDPSizeInPX()
    {
        float dip = 130f;
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        int result = Math.round(px);
        return result;
    }

    private void viewImage(String imagePath)
    {
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(EditVersionDataActivity.this, ViewOrDeleteImageActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VersionData_Intent_Name, newVersionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.ImageData_Intent_Path, imagePath);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, false);
        startActivity(i);
    }




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
        // Name of image
        String imageFileName = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_" + imageCounter;
        // Location: Phone > Android > data > com.example.sda_a5_2024_bradleyweibel > files > Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        String fullPathString = StringHelper.filePath + "/";
        String currentFileName = currentPhotoPath.replace(fullPathString, "");
        // Add name of image to list of newly created images
        listOfNewImageNames.add(currentFileName);
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
            ImageView imageView = new ImageView(EditVersionDataActivity.this);
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



    // --------------------------------------------- Cleanup
    private void removeAllNewImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (listOfNewImageNames.contains(currentFileName))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    imageFile.delete();
                }
            }
        }
    }

    private void renameImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null) {
            String fullPathString = StringHelper.filePath + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    String newFileName = currentFile.getPath();
                    newFileName = newFileName.replace(imagePrefix, StringHelper.Image_Prefix + songName + "_" + newVersionName + "_");
                    // Rename file with new version name
                    File newNameImageFile = new File(newFileName);
                    imageFile.renameTo(newNameImageFile);
                }
            }
        }
    }



    // --------------------------------------------- Permission handling
    // Check if permissions are already granted
    private Boolean isCameraPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionDataActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isWriteExternalPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionDataActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isReadExternalPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionDataActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }

    // Asking for permission
    private void askCameraPermission() { ActivityCompat.requestPermissions(EditVersionDataActivity.this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_CODE); }
    private void askWriteStoragePermission() { ActivityCompat.requestPermissions(EditVersionDataActivity.this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE); }
    private void askReadStoragePermission() { ActivityCompat.requestPermissions(EditVersionDataActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE); }

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
            StringHelper.showToast("Please accept all permission prompts to use these features", EditVersionDataActivity.this);
        }
    }
}
