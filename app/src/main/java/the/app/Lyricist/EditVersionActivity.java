package the.app.Lyricist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditVersionActivity extends AppCompatActivity
{
    // Variables for UI elements
    private TextView songNameTxt;
    private EditText versionNameEdt, versionDescriptionEdt, versionLyricsEdt;
    private FloatingActionButton saveBtn, deleteBtn, backToViewVersionBtn;
    private ImageButton galleryImageBtn, newImageBtn, galleryVideoBtn, newVideoBtn, galleryAudioBtn, newAudioBtn;
    private LinearLayout imageContainerLyt, videoContainerLyt, recordingContainerLyt;

    // General variables
    private Integer versionId, imageCounter, videoCounter, recordingCounter;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private Boolean wasPreviousScreenAViewer;
    private String songName, newVersionName, originalVersionName, versionDescription, versionLyrics, currentPhotoPath, imageStandardNamePrefix, currentVideoPath, videoStandardNamePrefix, currentRecordingPath, audioStandardNamePrefix;
    private ArrayList<String> listOfNewImageNames, listOfNewVideoNames, listOfNewAudioNames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_version);
        // Set file paths for saving images, videos, and audio recordings
        StringHelper.setFolderPaths(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)), String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MOVIES)), String.valueOf(getExternalFilesDir(Environment.DIRECTORY_MUSIC)));

        // Initializing all our UI element variables
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
        recordingContainerLyt = findViewById(R.id.idLytRecordingContainer);
        galleryAudioBtn = findViewById(R.id.idAddGalleryAudioBtn);
        newAudioBtn = findViewById(R.id.idAddNewAudioBtn);
        saveBtn = findViewById(R.id.idBtnSaveVersion);
        deleteBtn = findViewById(R.id.idBtnDeleteVersion);
        backToViewVersionBtn = findViewById(R.id.idBtnBack);

        // Get all intent data
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        // If returning from Image/video/audio viewer, more intent data is passed
        newVersionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        wasPreviousScreenAViewer = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_View_Screen, false);
        listOfNewImageNames = getIntent().getStringArrayListExtra(StringHelper.ImageData_Intent_List_Of_New_Photos);
        listOfNewVideoNames = getIntent().getStringArrayListExtra(StringHelper.VideoData_Intent_List_Of_New_Videos);
        listOfNewAudioNames = getIntent().getStringArrayListExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings);

        // Creating a new DB handler class and passing our context to it
        dbHandler = new DBHandler(EditVersionActivity.this);
        // Get version data from DB
        versionData = dbHandler.getSongVersion(versionId);
        songName = dbHandler.getSongName(versionData.getVersionSongId());
        originalVersionName = versionData.getVersionName();

        // Insert values into UI elements
        songNameTxt.setText(songName);
        versionNameEdt.setText(originalVersionName);
        if (wasPreviousScreenAViewer)
            versionNameEdt.setText(newVersionName);
        else
        {
            // Previous screen was not a viewer (image, video, audio), overwrite intent data with DB data
            versionDescription = versionData.getVersionDescription();
            versionLyrics = versionData.getVersionLyrics();
        }
        versionDescriptionEdt.setText(versionDescription);
        versionLyricsEdt.setText(versionLyrics);

        // Images handling
        imageCounter = 1;
        imageStandardNamePrefix = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_";
        listOfNewImageNames = listOfNewImageNames == null ? new ArrayList<String>() : listOfNewImageNames;
        getVersionImages();

        // Videos handling
        videoCounter = 1;
        videoStandardNamePrefix = StringHelper.Video_Prefix + songName + "_" + originalVersionName + "_";
        listOfNewVideoNames = listOfNewVideoNames == null ? new ArrayList<String>() : listOfNewVideoNames;
        getVersionVideos();

        // Recordings handling
        recordingCounter = 1;
        audioStandardNamePrefix = StringHelper.Audio_Prefix + songName + "_" + originalVersionName + "_";
        listOfNewAudioNames = listOfNewAudioNames == null ? new ArrayList<String>() : listOfNewAudioNames;
        getVersionRecordings();

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
                        // Update images, videos and audio recordings with the new version name
                        renameImages();
                        renameVideos();
                        renameRecordings();
                    }
                }

                String modificationDate = StringHelper.getFormattedDate();
                // Update version of song in DB
                dbHandler.updateVersion(versionId, newVersionName, songName, versionDescription, versionLyrics, modificationDate);

                StringHelper.showToast(getString(R.string.toastr_version_updated), EditVersionActivity.this);

                // Go to view version screen
                Intent i = new Intent(EditVersionActivity.this, ViewVersionActivity.class);
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
                // Remove newly added but unsaved images, videos and audio recordings
                removeAllNewImages();
                removeAllNewVideos();
                removeAllNewRecordings();

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
                // Remove newly added but unsaved images, videos and audio recordings
                removeAllNewImages();
                removeAllNewVideos();
                removeAllNewRecordings();

                // opening a new activity via a intent.
                Intent i = new Intent(EditVersionActivity.this, ViewVersionActivity.class);
                // Passing the song name
                i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
                startActivity(i);
            }
        });

        // --------------------------------------------- Image handling
        // User clicks button wanting to take a new photo
        newImageBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isCameraPermissionGranted() && isWriteExternalPermissionGranted())
                    openImageCamera();
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

        // --------------------------------------------- Audio handling
        // User wants to take a new recording
        newAudioBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isWriteExternalPermissionGranted() && isAudioPermissionGranted())
                    openAudioRecorder();
                else
                {
                    if (!isWriteExternalPermissionGranted())
                        askWriteStoragePermission();
                    else if (!isAudioPermissionGranted())
                        askRecordAudioPermission();
                }
            }
        });
        // User wants to select a gallery audio recording
        galleryAudioBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isWriteExternalPermissionGranted())
                    openAudioGallery();
                else
                    askWriteStoragePermission();
            }
        });
    }

    // Get list of already created images, videos, audio clips for this version
    private void getVersionImages()
    {
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            String imagePrefix = StringHelper.Image_Prefix + songName + "_" + originalVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(imagePrefix))
                {
                    // Image belonging to this song and version found
                    currentPhotoPath = currentFile.getPath();
                    insertNewImageIntoUI();
                }
            }
        }
    }

    // Fetches all videos created previously for this unsaved version and populates the UI
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
                    catch (IOException e)
                    {
                        Log.e("editVersionPage", "Failed to insert a previously created video into UI");
                    }
                }
            }
        }
    }

    // Fetches all audio files created previously for this unsaved version and populates the UI
    private void getVersionRecordings()
    {
        File file = new File(StringHelper.Audio_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Audio_Folder_Path + "/";
            String audioPrefix = StringHelper.Audio_Prefix + songName + "_" + originalVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(audioPrefix))
                {
                    // Recording belonging to this song and version found
                    currentRecordingPath = currentFile.getPath();
                    insertNewRecordingIntoUI();
                }
            }
        }
    }

    // --------------------------------------------- Creating new images in UI
    private void openImageCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                Log.e("editVersionPage", "Failed to create a new image file");
            }

            // If file was successfully created
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, StringHelper.App_Authority, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, NumberHelper.REQUEST_TAKE_PHOTO);
            }
        }
        else
            StringHelper.showToast(getString(R.string.toastr_no_camera_found), EditVersionActivity.this);
    }

    private void openImageGallery()
    {
        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent, NumberHelper.REQUEST_CHOOSE_PHOTO);
    }

    // Create image and save in phones storage
    private File createImageFile() throws IOException
    {
        // Name of image
        String imageFileName = imageStandardNamePrefix + imageCounter;
        File storageDir = new File(StringHelper.Image_Folder_Path);
        File image = File.createTempFile(imageFileName, StringHelper.Image_Suffix_With_Dot, storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        addNewImageToList();
        return image;
    }

    // Create UI element to show image
    private void insertNewImageIntoUI()
    {
        // Create new ImageView
        ImageView imageView = new ImageView(EditVersionActivity.this);

        // The following code was developed with the aid of ChatGPT after constant failed attempts
        // <<<<<<< Start of Chat GPT aided code >>>>>>>>
        // Calculate the dimensions for scaling down the bitmap
        int targetWidth = NumberHelper.imageViewDPSizeInPX(getResources());
        int targetHeight = NumberHelper.imageViewDPSizeInPX(getResources());
        // Decode the bitmap from the file, scaled down to the target dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;
        int scaleFactor = Math.min(imageWidth / targetWidth, imageHeight / targetHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, options);
        // <<<<<<< End of Chat GPT aided code >>>>>>>>

        // Set the parameters
        int dimensions = NumberHelper.imageViewDPSizeInPX(getResources());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
        // Set the margin in linearlayout
        params.setMargins(NumberHelper.Image_Margin_Left, NumberHelper.Image_Margin_Top, NumberHelper.Image_Margin_Right, NumberHelper.Image_Margin_Bottom);
        imageView.setLayoutParams(params);
        // Insert the bitmap rather than the full image
        imageView.setImageBitmap(bitmap);
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
        i.putExtra(StringHelper.ImageData_Intent_List_Of_New_Photos, listOfNewImageNames);
        i.putExtra(StringHelper.VideoData_Intent_List_Of_New_Videos, listOfNewVideoNames);
        i.putExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings, listOfNewAudioNames);
        startActivity(i);
    }

    // --------------------------------------------- Creating new videos in UI
    private void openVideoCamera()
    {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takeVideoIntent, NumberHelper.REQUEST_TAKE_VIDEO);
        else
            StringHelper.showToast(getString(R.string.toastr_no_camera_found), EditVersionActivity.this);
    }

    private void openVideoGallery()
    {
        Intent chooseVideoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseVideoIntent, NumberHelper.REQUEST_CHOOSE_VIDEO);
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
            Bitmap thumbnail = retriever.getFrameAtTime(NumberHelper.Video_Get_Frame_At_1_Second, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            // Create a new ImageView
            ImageView imageView = new ImageView(this);
            imageView.setTag(currentVideoPath);
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
            // Move to next video
            videoCounter+=1;
            // <<<<<<< End of Chat GPT aided code >>>>>>>>
        }
        catch (Exception e)
        {
            Log.e("editVersionPage", "Failed to insert a new video into UI");
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
        newVersionName = versionNameEdt.getText().toString().trim();
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(EditVersionActivity.this, ViewOrDeleteVideoActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VersionData_Intent_Name, newVersionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.VideoData_Intent_Path, videoPath);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, false);
        i.putExtra(StringHelper.ImageData_Intent_List_Of_New_Photos, listOfNewImageNames);
        i.putExtra(StringHelper.VideoData_Intent_List_Of_New_Videos, listOfNewVideoNames);
        i.putExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings, listOfNewAudioNames);
        startActivity(i);
    }

    // --------------------------------------------- Creating new audio recordings in UI
    private void openAudioRecorder()
    {
        newVersionName = versionNameEdt.getText().toString().trim();
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(EditVersionActivity.this, RecordOrPlayAudioActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VersionData_Intent_Name, newVersionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.AudioData_Intent_Counter_Value, recordingCounter);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, false);
        i.putExtra(StringHelper.ImageData_Intent_List_Of_New_Photos, listOfNewImageNames);
        i.putExtra(StringHelper.VideoData_Intent_List_Of_New_Videos, listOfNewVideoNames);
        i.putExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings, listOfNewAudioNames);
        startActivity(i);
    }

    private void openAudioGallery()
    {
        Intent chooseAudioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseAudioIntent, NumberHelper.REQUEST_CHOOSE_RECORDING);
    }

    // Listen to audio recording in viewer screen
    private void viewAudioRecording(String recordingPath)
    {
        newVersionName = versionNameEdt.getText().toString().trim();
        versionDescription = versionDescriptionEdt.getText().toString().trim();
        versionLyrics = versionLyricsEdt.getText().toString().trim();

        Intent i = new Intent(EditVersionActivity.this, RecordOrPlayAudioActivity.class);
        // Passing the needed variables that will be needed to return and reopen this screen - no yet unsaved data the user has entered must be lost
        i.putExtra(StringHelper.SongData_Intent_Name, songName);
        i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VersionData_Intent_Name, newVersionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.AudioData_Intent_Path, recordingPath);
        i.putExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings, listOfNewAudioNames);
        i.putExtra(StringHelper.VersionData_Intent_Add_Screen, false);
        i.putExtra(StringHelper.ImageData_Intent_List_Of_New_Photos, listOfNewImageNames);
        i.putExtra(StringHelper.VideoData_Intent_List_Of_New_Videos, listOfNewVideoNames);
        i.putExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings, listOfNewAudioNames);
        startActivity(i);
    }

    // Create UI element to show recording
    private void insertNewRecordingIntoUI()
    {
        // Create new ImageView
        ImageView imageView = new ImageView(EditVersionActivity.this);
        // Set the parameters
        int dimensions = NumberHelper.imageViewDPSizeInPX(getResources());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimensions, dimensions);
        // Set the margin in linearlayout
        params.setMargins(NumberHelper.Image_Margin_Left, NumberHelper.Image_Margin_Top, NumberHelper.Image_Margin_Right, NumberHelper.Image_Margin_Bottom);
        imageView.setLayoutParams(params);
        // Insert the image (alternate between two images)
        imageView.setImageDrawable(getDrawable(NumberHelper.isNumberEven(recordingCounter) ? R.drawable.audio_photo_1 : R.drawable.audio_photo_2));
        imageView.setTag(currentRecordingPath);
        imageView.setOnClickListener(v -> { viewAudioRecording(v.getTag().toString()); });
        // Insert ImageView into UI
        recordingContainerLyt.addView(imageView);
        recordingCounter += 1;
    }

    // --------------------------------------------- After successful image, video, or audio action
    // After a photo/video has been taken/chosen
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NumberHelper.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
        {
            // Image was successfully taken with the camera and created in file location
            File imageFile = new File(currentPhotoPath);
            insertNewImageIntoUI();
        }
        else if (requestCode == NumberHelper.REQUEST_CHOOSE_PHOTO && resultCode == RESULT_OK)
        {
            // Photo was chosen from gallery
            // The following code was developed side-by-side with the help of ChatGPT after hours of failing to figure this out
            // <<<<<<< Start of Chat GPT aided code >>>>>>>>
            // Get the uri of the chosen image
            Uri imageLocation = data.getData();
            String imageFileName = imageStandardNamePrefix + imageCounter;
            File storageDir = new File(StringHelper.Image_Folder_Path);
            File newFile = null;
            try
            {
                String fileExt = StringHelper.getFileExtension(imageLocation, getContentResolver());
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
            catch (IOException e)
            {
                Log.e("editVersionPage", "Failed to insert a gallery image into UI");
            }
            // Set the 'currentPhotoPath' to the path of the newly created image file
            if (newFile != null)
            {
                currentPhotoPath = newFile.getAbsolutePath();
                addNewImageToList();
                insertNewImageIntoUI();
            }
            // <<<<<<< End of Chat GPT aided code >>>>>>>>
        }
        else if (requestCode == NumberHelper.REQUEST_TAKE_VIDEO && resultCode == RESULT_OK)
        {
            // Video was successfully taken with the video camera
            // The following code was developed with input from ChatGPT
            // <<<<<<< Start of Chat GPT aided code >>>>>>>>
            Uri videoLocation = data.getData();
            if (videoLocation != null)
            {
                try
                {
                    // Get a file descriptor from the video URI
                    ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(videoLocation, "r");
                    if (parcelFileDescriptor != null)
                    {
                        // Create an input stream from the file descriptor
                        FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                        // Create a file in external storage (Movies directory)
                        File storageDir = new File(StringHelper.Video_Folder_Path);
                        String videoFileName = videoStandardNamePrefix + videoCounter + StringHelper.Video_Suffix_With_Dot;
                        File externalFile = new File(storageDir, videoFileName);
                        // Create an output stream to the external file
                        FileOutputStream outputStream = new FileOutputStream(externalFile);
                        // Copy the video data from the input stream to the output stream
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0)
                        {
                            outputStream.write(buffer, 0, length);
                        }
                        // Close the streams
                        inputStream.close();
                        outputStream.close();
                        // <<<<<<< End of Chat GPT aided code >>>>>>>>

                        currentVideoPath = externalFile.getAbsolutePath();
                        addNewVideoToList();
                        insertNewVideoIntoUI(videoLocation);
                    }
                }
                catch (IOException e)
                {
                    Log.e("editVersionPage", "Failed to insert a new video into UI");
                }
            }
        }
        else if (requestCode == NumberHelper.REQUEST_CHOOSE_VIDEO && resultCode == RESULT_OK)
        {
            // The following code was developed side-by-side with the help of ChatGPT
            // <<<<<<< Start of Chat GPT aided code >>>>>>>>
            // Video was successfully chosen from gallery
            Uri videoLocation = data.getData();
            String videoFileName = videoStandardNamePrefix + videoCounter;
            File storageDir = new File(StringHelper.Video_Folder_Path);
            File newFile = null;
            try
            {
                String fileExt = StringHelper.getFileExtension(videoLocation, getContentResolver());
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
            catch (IOException e)
            {
                Log.e("editVersionPage", "Failed to copy a gallery video into file folder");
            }
            // Set the current video path to the path of the newly created file
            if (newFile != null)
            {
                currentVideoPath = newFile.getAbsolutePath();
                addNewVideoToList();
                try
                {
                    insertNewVideoIntoUI(videoLocation);
                }
                catch (IOException e)
                {
                    Log.e("editVersionPage", "Failed to insert a gallery video into UI");
                }
            }
            // <<<<<<< End of Chat GPT aided code >>>>>>>>
        }
        else if (requestCode == NumberHelper.REQUEST_CHOOSE_RECORDING && resultCode == RESULT_OK)
        {
            // Audio recording was successfully chosen from gallery
            Uri audioLocation = data.getData();
            String audioFileName = audioStandardNamePrefix + recordingCounter;
            File storageDir = new File(StringHelper.Audio_Folder_Path);
            File newFile = null;
            try
            {
                String fileExt = StringHelper.getFileExtension(audioLocation, getContentResolver());
                File tempVideoFile = File.createTempFile(audioFileName, "." + fileExt, storageDir);
                newFile = new File(tempVideoFile.getAbsolutePath());

                // Copy the audio recording data from the selected URI to the new file
                InputStream inputStream = getContentResolver().openInputStream(audioLocation);
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
            catch (IOException e)
            {
                Log.e("editVersionPage", "Failed to copy a gallery recording/song into file folder");
            }
            // Set the current video path to the path of the newly created file
            if (newFile != null)
            {
                currentRecordingPath = newFile.getAbsolutePath();
                addNewAudioToList();
                insertNewRecordingIntoUI();
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
                if (listOfNewImageNames.contains(currentFileName))
                {
                    // Image belonging to this song and version found
                    File imageFile = new File(currentFile.getPath());
                    imageFile.delete();
                }
            }
        }
    }

    // Rename all newly created images in file system
    private void renameImages()
    {
        File file = new File(StringHelper.Image_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Image_Folder_Path + "/";
            String newImageNamePrefix = StringHelper.Image_Prefix + songName + "_" + newVersionName + "_";
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

    // Remove all newly created video files from file system
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
                if (listOfNewVideoNames.contains(currentFileName))
                {
                    // Video belonging to this song and version found
                    File videoFile = new File(currentFile.getPath());
                    videoFile.delete();
                }
            }
        }
    }

    // Rename all newly created videos in file system
    private void renameVideos()
    {
        File file = new File(StringHelper.Video_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Video_Folder_Path + "/";
            String newVideoNamePrefix = StringHelper.Video_Prefix + songName + "_" + newVersionName + "_";
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

    // Remove all newly created audio files from file system
    private void removeAllNewRecordings()
    {
        File file = new File(StringHelper.Audio_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Audio_Folder_Path + "/";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (listOfNewAudioNames.contains(currentFileName))
                {
                    // Audio belonging to this song and version found
                    File audioFile = new File(currentFile.getPath());
                    audioFile.delete();
                }
            }
        }
    }

    // Rename all newly created audio files in file system
    private void renameRecordings()
    {
        File file = new File(StringHelper.Audio_Folder_Path);
        File[] files = file.listFiles();
        if (files != null)
        {
            String fullPathString = StringHelper.Audio_Folder_Path + "/";
            String newAudioNamePrefix = StringHelper.Audio_Prefix + songName + "_" + newVersionName + "_";
            for (File currentFile : files)
            {
                String currentFileName = currentFile.getPath().replace(fullPathString, "");
                if (currentFileName.startsWith(audioStandardNamePrefix))
                {
                    // Audio belonging to this song and version found
                    File audioFile = new File(currentFile.getPath());
                    String newFileName = currentFile.getPath();
                    newFileName = newFileName.replace(audioStandardNamePrefix, newAudioNamePrefix);
                    // Rename file with new version name
                    File newNameAudioFile = new File(newFileName);
                    audioFile.renameTo(newNameAudioFile);
                }
            }
        }
    }

    // --------------------------------------------- Permission handling
    // Check if permissions are already granted
    private Boolean isCameraPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionActivity.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED; }

    private Boolean isWriteExternalPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED; }

    private Boolean isAudioPermissionGranted() { return ContextCompat.checkSelfPermission(EditVersionActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED; }

    private void askCameraPermission() { ActivityCompat.requestPermissions(EditVersionActivity.this, new String[] {android.Manifest.permission.CAMERA}, NumberHelper.REQUEST_CODE); }

    private void askWriteStoragePermission() { ActivityCompat.requestPermissions(EditVersionActivity.this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, NumberHelper.REQUEST_CODE); }

    private void askRecordAudioPermission() { ActivityCompat.requestPermissions(EditVersionActivity.this, new String[] {Manifest.permission.RECORD_AUDIO}, NumberHelper.REQUEST_CODE); }

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
            StringHelper.showToast(getString(R.string.toastr_permissions_warning), EditVersionActivity.this);
    }

    // --------------------------------------------- Helpers
    private void addNewImageToList()
    {
        // Differentiate these new images by saving their names
        String fullPathString = StringHelper.Image_Folder_Path + "/";
        String currentFileName = currentPhotoPath.replace(fullPathString, "");
        // Add name of image to list of newly created images
        listOfNewImageNames.add(currentFileName);
    }

    private void addNewVideoToList()
    {
        // Differentiate these new videos by saving their names
        String fullPathString = StringHelper.Video_Folder_Path + "/";
        String currentFileName = currentVideoPath.replace(fullPathString, "");
        // Add name of image to list of newly created images
        listOfNewVideoNames.add(currentFileName);
    }

    private void addNewAudioToList()
    {
        // Differentiate these new audio recordings by saving their names
        String fullPathString = StringHelper.Audio_Folder_Path + "/";
        String currentFileName = currentRecordingPath.replace(fullPathString, "");
        // Add name of recording to list of newly created recordings
        listOfNewAudioNames.add(currentFileName);
    }
}
