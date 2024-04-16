package com.example.sda_a5_2024_bradleyweibel;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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

public class AddVersionActivity extends AppCompatActivity
{
    // UI elements
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton createBtn, backBtn;
    private LinearLayout imageContainerLyt, videoContainerLyt;
    private ImageButton galleryImageBtn, newImageBtn, galleryVideoBtn, newVideoBtn, galleryAudioBtn, newAudioBtn;

    // General variables
    private Integer imageCounter, videoCounter;
    private DBHandler dbHandler;
    private Boolean wasPreviousScreenAViewer;
    private String songName, versionName, versionDescription, versionLyrics, currentPhotoPath, imageStandardNamePrefix, currentVideoPath, videoStandardNamePrefix;

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
        setContentView(R.layout.activity_add_version);

        // Mapping local variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        versionNameEdt = findViewById(R.id.idEdtVersionName);
        versionDescriptionEdt = findViewById(R.id.idEdtVersionDescription);
        versionLyricsEdt = findViewById(R.id.idEdtVersionLyrics);
        imageContainerLyt = findViewById(R.id.idLytImageContainer);
        galleryImageBtn = findViewById(R.id.idAddGalleryImageBtn);
        newImageBtn = findViewById(R.id.idAddNewImageBtn);
        videoContainerLyt = findViewById(R.id.idLytVideoContainer);
        galleryVideoBtn = findViewById(R.id.idAddGalleryVideoBtn);
        newVideoBtn = findViewById(R.id.idAddNewVideoBtn);
        //galleryAudioBtn = findViewById(R.id.idAddGalleryAudioBtn);
        //newAudioBtn = findViewById(R.id.idAddNewAudioBtn);
        createBtn = findViewById(R.id.idBtnAddVersion);
        backBtn = findViewById(R.id.idBtnBack);

        // Get all intent data
        songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        // If returning from Image viewer, more intent data is passed
        versionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        wasPreviousScreenAViewer = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_View_Screen, false);

        // Initiate image variables
        imageCounter = 1;
        imageStandardNamePrefix = StringHelper.Image_Prefix + songName + "_" + StringHelper.Placeholder_Version_Name + "_";

        // Initiate video variables
        videoCounter = 1;
        videoStandardNamePrefix = StringHelper.Video_Prefix + songName + "_" + StringHelper.Placeholder_Version_Name + "_";

        // Populate UI elements
        songNameTxt.setText(songName);
        if (wasPreviousScreenAViewer)
        {
            versionNameEdt.setText(versionName);
            versionDescriptionEdt.setText(versionDescription);
            versionLyricsEdt.setText(versionLyrics);
            getVersionImages();
            getVersionVideos();
        }

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(AddVersionActivity.this);

        final Integer[] songId = {dbHandler.getSongId(songName)};
        final Integer[] versionId = {0};

        // Add on click listener for add song and version button
        createBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get data from all edit text fields
                versionName = versionNameEdt.getText().toString().trim();
                versionDescription = versionDescriptionEdt.getText().toString().trim();
                versionLyrics = versionLyricsEdt.getText().toString().trim();

                String creationDate = StringHelper.getFormattedDate();

                if (versionName.isEmpty())
                {
                    // Version name field is not populated
                    StringHelper.showToast(getString(R.string.toastr_missing_version_name), AddVersionActivity.this);
                    return;
                }
                else if (songId[0].equals(0))
                {
                    // Song doesn't exist yet
                    // Add new song to DB
                    songId[0] = dbHandler.addNewSong(songName, creationDate, creationDate);
                    // Add new version of song to DB
                    versionId[0] = dbHandler.addNewVersion(versionName, songId[0], versionDescription, versionLyrics, creationDate, creationDate);

                    StringHelper.showToast(getString(R.string.toastr_song_and_version_added), AddVersionActivity.this);
                }
                else
                {
                    // Song already exists, add a new version
                    // Version name must be unique in the context of this song
                    if (!dbHandler.isVersionNameUnique(songId[0], versionName))
                    {
                        StringHelper.showToast(getString(R.string.toastr_unique_version_name), AddVersionActivity.this);
                        return;
                    }
                    else
                    {
                        // Add new version of song to DB
                        versionId[0] = dbHandler.addNewVersion(versionName, songId[0], versionDescription, versionLyrics, creationDate, creationDate);
                    }
                }

                // TODO: rename images, videos and audio clips named with the placeholder
                renameImages();
                renameVideos();

                // Go to view version screen
                Intent i = new Intent(AddVersionActivity.this, ViewVersionActivity.class);
                // Pass data through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId[0]);
                startActivity(i);
            }
        });

        // Back to version button is clicked
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO: remove any newly created images, videos or audio clips
                removeAllNewImages();
                removeAllNewVideos();

                Intent i;
                if (songId[0].equals(0))
                {
                    // Song has not been created yet, go back to create song screen
                    // Opening a new activity via an intent
                    i = new Intent(AddVersionActivity.this, AddSongActivity.class);
                }
                else
                {
                    // Song has been created already, go back to 'song and versions' screen
                    // Opening a new activity via an intent
                    i = new Intent(AddVersionActivity.this, ViewSongAndVersionsActivity.class);
                }
                // Passing the song name
                i.putExtra(StringHelper.SongData_Intent_Name, songNameTxt.getText().toString());
                startActivity(i);
            }
        });

        // --------------------------------------------- Image handling
        // User wants to take a new photo
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
                    openImageGallery();
                else
                    askWriteStoragePermission();
            }
        });

        // --------------------------------------------- Video handling
        // User wants to take a new video
        newVideoBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isCameraPermissionGranted() && isWriteExternalPermissionGranted() && isAudioPermissionGranted())
                    openVideoCamera();
                else
                {
                    if (!isCameraPermissionGranted())
                        askCameraPermission();
                    else if (!isWriteExternalPermissionGranted())
                        askWriteStoragePermission();
                    else if (!isAudioPermissionGranted())
                        askRecordAudioPermission();
                }
            }
        });
        // User wants to select a gallery image
        galleryVideoBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isWriteExternalPermissionGranted())
                    openVideoGallery();
                else
                    askWriteStoragePermission();
            }
        });
    }

    // Get list of already created images, videos for this version
    private void getVersionImages()
    {
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imageStandardNamePrefix))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    currentPhotoPath = currentFile.getPath();
                    insertNewImageIntoUI(Uri.fromFile(imageFile));
                }
            }
        }
    }
    private void getVersionVideos()
    {
        File file = new File(StringHelper.Video_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Video_Folder_Path + "/";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(videoStandardNamePrefix))
                {
                    // Video belonging to this song and version found
                    File videoFile = new File(currentFile.getPath());
                    currentVideoPath = currentFile.getPath();
                    try
                    {
                        insertNewVideoIntoUI(Uri.fromFile(videoFile));
                    }
                    catch (IOException e) {}
                }
            }
        }
    }

    // --------------------------------------------- Creating new images in UI
    // Camera permission granted, proceed with image capture (and later saving)
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
    private void openImageGallery()
    {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, REQUEST_CHOOSE_PHOTO);
    }

    // Create image and save in phones storage
    private File createImageFile() throws IOException
    {
        String imageFileName = imageStandardNamePrefix + imageCounter;
        // Location: Phone > Android > data > com.example.sda_a5_2024_bradleyweibel > files > Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, StringHelper.Image_Suffix_With_Dot, storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    // Create UI element to show image
    private void insertNewImageIntoUI(Uri fileLocation)
    {
        // Create new ImageView
        ImageView imageView = new ImageView(AddVersionActivity.this);
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
        versionName = versionNameEdt.getText().toString().trim();
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(AddVersionActivity.this, ViewOrDeleteImageActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.ImageData_Intent_Path, imagePath);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, true);
        startActivity(i);
    }

    // --------------------------------------------- Creating new videos in UI
    private void openVideoCamera()
    {
        // TODO
        StringHelper.showToast("Open video camera", AddVersionActivity.this);
    }
    private void openVideoGallery()
    {
        Intent chooseVideoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseVideoIntent, REQUEST_CHOOSE_VIDEO);
    }

    // Create UI element to show video
    private void insertNewVideoIntoUI(Uri fileLocation) throws IOException
    {
        // The following code was developed side-by-side with the help of ChatGPT
        // <<<<<<< Start of Chat GPT aided code >>>>>>>>
        // Create a MediaMetadataRetriever
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try
        {
            // Set the data source to the video location
            retriever.setDataSource(this, fileLocation);
            // Extract a frame at the specified time (e.g., 1 second)
            Bitmap thumbnail = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            // Create a new ImageView
            ImageView imageView = new ImageView(this);
            imageView.setTag(currentVideoPath);
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
            // Move to next video
            videoCounter+=1;
            // <<<<<<< End of Chat GPT aided code >>>>>>>>
        }
        catch (Exception e)
        {
            // Handle exceptions
            e.printStackTrace();
        }
        finally
        {
            // Release the MediaMetadataRetriever
            retriever.release();
        }
    }
    // Open screen with large video
    private void viewVideo(String videoPath)
    {
        versionName = versionNameEdt.getText().toString().trim();
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(AddVersionActivity.this, ViewOrDeleteVideoActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.VideoData_Intent_Path, videoPath);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, true);
        startActivity(i);
    }

    // --------------------------------------------- After successful image, video, or audio action
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
            // <<<<<<< Start of Chat GPT aided code >>>>>>>>
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
            // <<<<<<< End of Chat GPT aided code >>>>>>>>

            insertNewImageIntoUI(imageLocation);
        }
        else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK)
        {
            // Video was successfully taken with the camera
            StringHelper.showToast("Video successfully taken with the camera", AddVersionActivity.this);
        }
        else if (requestCode == REQUEST_CHOOSE_VIDEO && resultCode == RESULT_OK)
        {
            // The following code was developed side-by-side with the help of ChatGPT
            // <<<<<<< Start of Chat GPT aided code >>>>>>>>
            // Video was successfully chosen from gallery
            Uri videoLocation = data.getData();
            String videoFileName = videoStandardNamePrefix + videoCounter;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File newFile = null;
            try
            {
                String fileExt = getFileExtension(videoLocation);
                File tempVideoFile = File.createTempFile(videoFileName, "." + fileExt, storageDir);
                newFile = new File(tempVideoFile.getAbsolutePath());

                // Copy the video data from the selected URI to the new file
                InputStream inputStream = getContentResolver().openInputStream(videoLocation);
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
            // Set the current video path to the path of the newly created file
            if (newFile != null)
                currentVideoPath = newFile.getAbsolutePath();
            // <<<<<<< End of Chat GPT aided code >>>>>>>>

            try
            {
                insertNewVideoIntoUI(videoLocation);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // --------------------------------------------- Cleanup
    private void removeAllNewImages()
    {
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imageStandardNamePrefix))
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
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            String newImageNamePrefix = StringHelper.Image_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files)
            {
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
    private void removeAllNewVideos()
    {
        File file = new File(StringHelper.Video_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Video_Folder_Path + "/";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(videoStandardNamePrefix))
                {
                    // Video belonging to this song and version found
                    File videoFile = new File(currentFile.getPath());
                    videoFile.delete();
                }
            }
        }
    }
    private void renameVideos()
    {
        File file = new File(StringHelper.Video_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Video_Folder_Path + "/";
            String newVideoNamePrefix = StringHelper.Video_Prefix + songName + "_" + versionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(videoStandardNamePrefix))
                {
                    // Video belonging to this song and version found
                    File videoFile = new File(currentFile.getPath());
                    String newFileName = currentFile.getPath();
                    newFileName = newFileName.replace(videoStandardNamePrefix, newVideoNamePrefix);
                    // Rename file with new version name
                    File newNameVideoFile = new File(newFileName);
                    videoFile.renameTo(newNameVideoFile);
                }
            }
        }
    }

    // --------------------------------------------- Permission handling
    private Boolean isCameraPermissionGranted() { return ContextCompat.checkSelfPermission(AddVersionActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isWriteExternalPermissionGranted() { return ContextCompat.checkSelfPermission(AddVersionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }
    private Boolean isAudioPermissionGranted() { return ContextCompat.checkSelfPermission(AddVersionActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED; }
    private void askCameraPermission() { ActivityCompat.requestPermissions(AddVersionActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CODE); }
    private void askWriteStoragePermission() { ActivityCompat.requestPermissions(AddVersionActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE); }
    private void askRecordAudioPermission() { ActivityCompat.requestPermissions(AddVersionActivity.this, new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_CODE); }

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
            else if (!isAudioPermissionGranted())
                askRecordAudioPermission();
        }
        else
            StringHelper.showToast(getString(R.string.permissions_toastr_warning), AddVersionActivity.this);
    }

    // --------------------------------------------- Helpers
    private String getFileExtension(Uri fileContentUri)
    {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(fileContentUri));
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
