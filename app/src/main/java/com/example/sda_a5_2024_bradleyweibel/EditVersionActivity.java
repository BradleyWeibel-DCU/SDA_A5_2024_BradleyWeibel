package com.example.sda_a5_2024_bradleyweibel;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditVersionActivity extends AppCompatActivity
{
    // UI elements
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton saveBtn, deleteBtn, backToViewVersionBtn;
    private ImageButton galleryImageBtn, newImageBtn, galleryVideoBtn, newVideoBtn, galleryAudioBtn, newAudioBtn;
    private LinearLayout imageContainerLyt;

    // General variables
    private Integer versionId, imageCounter;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private Boolean wasPreviousScreenImageViewer;
    private String songName, newVersionName, originalVersionName, versionDescription, versionLyrics, currentPhotoPath, imageStandardNamePrefix;
    private ArrayList<String> listOfNewImageNames;

    // Static keys
    private static final int REQUEST_CODE = 100;
    private static final int REQUEST_CHOOSE_PHOTO = 101;
    private static final int REQUEST_TAKE_PHOTO = 102;
    private static final int REQUEST_CHOOSE_VIDEO = 103;
    private static final int REQUEST_TAKE_VIDEO = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_version);

        // Initializing all our variables
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameEdt = findViewById(R.id.idEdtVersionName);
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
        backToViewVersionBtn = findViewById(R.id.idBtnBack);

        // Get all intent data
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        // If returning from Image viewer, more intent data is passed
        newVersionName =getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        wasPreviousScreenImageViewer = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_View_Screen, false);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(EditVersionActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        songName = dbHandler.getSongName(versionData.getVersionSongId());
        originalVersionName = versionData.getVersionName();

        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameEdt.setText(originalVersionName);
        if (wasPreviousScreenImageViewer)
            versionNameEdt.setText(newVersionName);
        else
        {
            // Previous screen was not an image viewer, overwrite intent data with DB data
            versionDescription = versionData.getVersionDescription();
            versionLyrics = versionData.getVersionLyrics();
        }
        versionDescriptionEdt.setText(versionDescription);
        versionLyricsEdt.setText(versionLyrics);

        // Image handling
        imageCounter = 1;
        imageStandardNamePrefix = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_";
        listOfNewImageNames = new ArrayList<String>();
        getVersionImages();

        // On click listener for save version changes button
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                newVersionName = versionNameEdt.getText().toString().trim();
                versionDescription = versionDescriptionEdt.getText().toString().trim();
                versionLyrics = versionLyricsEdt.getText().toString().trim();

                // Validate if the name field is empty or not
                if (newVersionName.isEmpty())
                {
                    StringHelper.showToast(getString(R.string.toastr_missing_version_name), EditVersionActivity.this);
                    return;
                }
                else if (!newVersionName.equals(originalVersionName))
                {
                    if (!dbHandler.isVersionNameUnique(versionData.getVersionSongId(), newVersionName))
                    {
                        StringHelper.showToast(getString(R.string.toastr_unique_version_name), EditVersionActivity.this);
                        return;
                    }
                    else
                    {
                        // TODO: update images, videos and audio with new name
                        renameImages();
                    }
                }

                String modificationDate = StringHelper.getFormattedDate();
                // Update version of song in DB
                dbHandler.updateVersion(versionId, newVersionName, songName, versionDescription, versionLyrics, modificationDate);

                StringHelper.showToast(getString(R.string.toastr_version_updated), EditVersionActivity.this);

                // Go to view version screen
                Intent i = new Intent(EditVersionActivity.this, ViewVersionDataActivity.class);
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
                // TODO: remove newly added but unsaved images, videos and audio clips
                removeAllNewImages();

                // Opening a new activity via an intent
                Intent i = new Intent(EditVersionActivity.this, DeleteSongOrVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                i.putExtra(StringHelper.VersionData_Intent_Name, originalVersionName);
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });

        // Back to edit version button is clicked
        backToViewVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: remove newly added but unsaved images, videos and audio clips
                removeAllNewImages();

                // opening a new activity via a intent.
                Intent i = new Intent(EditVersionActivity.this, ViewVersionDataActivity.class);
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
                if (isCameraPermissionGranted() && isWriteExternalPermissionGranted())
                    openCamera();
                else
                {
                    if (!isCameraPermissionGranted())
                        askCameraPermission();
                    else if (!isWriteExternalPermissionGranted())
                        askWriteStoragePermission();
                }
            }
        });
        // User wants to select a gallery image
        galleryImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isWriteExternalPermissionGranted())
                    openGallery();
                else
                    askWriteStoragePermission();
            }
        });
    }

    // Get list of already created images for this version
    private void getVersionImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.filePath + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    currentPhotoPath = currentFile.getPath();
                    insertNewImageIntoUI(Uri.fromFile(imageFile));
                }
            }
        }
    }

    // --------------------------------------------- Creating new images in UI
    // Permission granted, proceed with image capture (and later saving)
    private void openCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex) {}

            // If file was successfully created
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, StringHelper.App_Authority, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    private void openGallery()
    {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, REQUEST_CHOOSE_PHOTO);
    }
    // After a photo/video has been taken/chosen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
        {
            // Image was successfully taken with the camera and created in file location
            File imageFile = new File(currentPhotoPath);
            Uri fileLocation = Uri.fromFile(imageFile);
            insertNewImageIntoUI(fileLocation);
        }
        else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK)
        {
            // Photo was chosen from gallery
            // The following code was developed side-by-side with the help of ChatGPT after hours of failing to figure this out
            // <<<<<<< Start of Chat GPT code >>>>>>>>
            // Get the uri of the chosen image
            Uri imageLocation = data.getData();
            String imageFileName = imageStandardNamePrefix + imageCounter;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File newFile = null;
            try
            {
                String fileExt = getFileExtension(imageLocation);
                File tempImageFile = File.createTempFile(imageFileName, "." + fileExt, storageDir);
                newFile = new File(tempImageFile.getAbsolutePath());

                // Copy the image data from the selected URI to the new file
                InputStream inputStream = getContentResolver().openInputStream(imageLocation);
                OutputStream outputStream = new FileOutputStream(newFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1)
                {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
            }
            catch (IOException e) {}
            // Set the 'currentPhotoPath' to the path of the newly created image file
            if (newFile != null)
                currentPhotoPath = newFile.getAbsolutePath();
            // <<<<<<< End of Chat GPT code >>>>>>>>
            addNewImageToList();
            insertNewImageIntoUI(imageLocation);
        }
        else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK)
        {
            // Video was successfully taken with the camera
        }
    }

    // Create image and save in phones storage
    private File createImageFile() throws IOException
    {
        // Name of image
        String imageFileName = imageStandardNamePrefix + imageCounter;
        // Location: Phone > Android > data > com.example.sda_a5_2024_bradleyweibel > files > Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, StringHelper.Image_Suffix_With_Dot, storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        addNewImageToList();
        return image;
    }
    // Create UI element to show image
    private void insertNewImageIntoUI(Uri fileLocation)
    {
        // Create new ImageView
        ImageView imageView = new ImageView(EditVersionActivity.this);
        // Set the parameters
        int dimensions = imageViewDPSizeInPX();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
        // Set the margin in linearlayout
        params.setMargins(0, 10, 0, 10);
        imageView.setLayoutParams(params);
        imageView.setImageURI(fileLocation);
        imageView.setTag(currentPhotoPath);
        imageView.setOnClickListener(v -> { viewImage(v.getTag().toString()); });
        // Insert ImageView into UI
        imageContainerLyt.addView(imageView);
        // Move to next image
        imageCounter+=1;
    }

    // Open screen with large image
    private void viewImage(String imagePath)
    {
        newVersionName = versionNameEdt.getText().toString().trim();
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(EditVersionActivity.this, ViewOrDeleteImageActivity.class);
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

    // --------------------------------------------- Cleanup
    private void removeAllNewImages()
    {
        File file = new File(StringHelper.filePath);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.filePath + "/";
            for (File currentFile : files)
            {
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
            String newImageNamePrefix = StringHelper.Image_Prefix + songName + "_" + newVersionName + "_";
            for (File currentFile : files) {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imageStandardNamePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    String newFileName = currentFile.getPath();
                    newFileName = newFileName.replace(imageStandardNamePrefix, newImageNamePrefix);
                    // Rename file with new version name
                    File newNameImageFile = new File(newFileName);
                    imageFile.renameTo(newNameImageFile);
                }
            }
        }
    }

    // --------------------------------------------- Permission handling
    // Check if permissions are already granted
    private Boolean isCameraPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isWriteExternalPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }
    private void askCameraPermission() { ActivityCompat.requestPermissions(EditVersionActivity.this, new String[] {android.Manifest.permission.CAMERA}, REQUEST_CODE); }
    private void askWriteStoragePermission() { ActivityCompat.requestPermissions(EditVersionActivity.this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE); }

    // Trigger on response to permission prompt from User
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (!isCameraPermissionGranted())
                askCameraPermission();
            else if (!isWriteExternalPermissionGranted())
                askWriteStoragePermission();
        }
        else
            StringHelper.showToast(getString(R.string.permissions_toastr_warning), EditVersionActivity.this);
    }

    // --------------------------------------------- Helpers
    private void addNewImageToList()
    {
        // Differentiate these new images by saving their names
        String fullPathString = StringHelper.filePath + "/";
        String currentFileName = currentPhotoPath.replace(fullPathString, "");
        // Add name of image to list of newly created images
        listOfNewImageNames.add(currentFileName);
    }
    private String getFileExtension(Uri imageContentUri)
    {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(imageContentUri));
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
