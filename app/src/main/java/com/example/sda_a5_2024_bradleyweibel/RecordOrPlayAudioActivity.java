package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordOrPlayAudioActivity extends AppCompatActivity
{
    // UI elements
    private TextView songNameTxt, headerTxt;
    private LinearLayout recordLyt, listenLyt, reloadLyt;
    private ImageButton recordBtn, playBtn, stopBtn, reloadBtn;
    private FloatingActionButton backBtn, saveBtn, deleteBtn;

    // General elements
    private Integer versionId, audioFileCounterValue;
    private Boolean recordingInProgress;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private DBHandler dbHandler;
    private VersionModal versionData;
    private Boolean wasPreviousScreenAddVersion;
    private String songName, versionName, versionDescription, versionLyrics, recordingOutput, recordingPath, audioNamePrefix;
    private ArrayList<String> listOfNewAudioNames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_or_play_audio);

        // Attaching local variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        headerTxt = findViewById(R.id.idTxtHeader);
        recordLyt = findViewById(R.id.idLytRecordContainer);
        listenLyt = findViewById(R.id.idLytPlayStopContainer);
        reloadLyt = findViewById(R.id.idLytReloadContainer);
        recordBtn = findViewById(R.id.idBtnStartStopRecording);
        playBtn = findViewById(R.id.idBtnPlay);
        stopBtn = findViewById(R.id.idBtnStop);
        reloadBtn = findViewById(R.id.idBtnReload);
        deleteBtn = findViewById(R.id.idBtnDelete);
        saveBtn = findViewById(R.id.idBtnSave);
        backBtn = findViewById(R.id.idBtnBack);

        // Get values from intent
        versionId = getIntent().getIntExtra(StringHelper.VersionData_Intent_ID, 0);
        songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        versionName = getIntent().getStringExtra(StringHelper.VersionData_Intent_Name);
        versionDescription = getIntent().getStringExtra(StringHelper.VersionData_Intent_Description);
        versionLyrics = getIntent().getStringExtra(StringHelper.VersionData_Intent_Lyrics);
        recordingPath = getIntent().getStringExtra(StringHelper.AudioData_Intent_Path);
        wasPreviousScreenAddVersion = getIntent().getBooleanExtra(StringHelper.VersionData_Intent_Add_Screen, false);
        audioFileCounterValue = getIntent().getIntExtra(StringHelper.AudioData_Intent_Counter_Value, 0);
        listOfNewAudioNames = getIntent().getStringArrayListExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings);

        recordingInProgress = false;
        if (recordingPath == null)
        {
            // No existing recording passed
            // Creating a new DB handler class and passing our context to it
            dbHandler = new DBHandler(RecordOrPlayAudioActivity.this);
            // Get version data from DB
            versionData = dbHandler.getSongVersion(versionId);
            // What version name must be used (depending on if the previous screen was an add or edit screen)
            String versionNamePlaceholder = wasPreviousScreenAddVersion ? StringHelper.Placeholder_Version_Name : versionData.getVersionName();
            audioNamePrefix = StringHelper.Audio_Prefix + songName + "_" + versionNamePlaceholder + "_" + audioFileCounterValue + NumberHelper.randomNumberGenerator();
            File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            recordingOutput = storageDirectory + "/" + audioNamePrefix + StringHelper.Audio_Suffix_With_Dot;
        }
        else
            recordingOutput = recordingPath;

        // Prepare UI elements
        songNameTxt.setText(songName);
        // Don't show the delete button if the previous screen was ViewVersion or if a new recording is to be made
        if (versionDescription == null || recordingPath == null)
        {
            deleteBtn.setVisibility(View.INVISIBLE);
            saveBtn.setVisibility(View.INVISIBLE);
        }
        if (recordingPath == null)
        {
            // New recording to be made, hide these elements for later
            playBtn.setVisibility(View.INVISIBLE);
            reloadBtn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            // Recording was passed, remove these UI elements
            ((ViewGroup) recordLyt.getParent()).removeView(recordLyt);
            ((ViewGroup) reloadLyt.getParent()).removeView(reloadLyt);
            saveBtn.setVisibility(View.INVISIBLE);
            headerTxt.setText(getString(R.string.play_recording_message));
            playRecording();
        }

        // Delete button is clicked
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Remove file if a new recording was made
                File audioFile = new File(recordingPath);
                audioFile.delete();

                returnToPreviousScreen();
            }
        });

        // Save button is clicked
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (listOfNewAudioNames != null)
                {
                    // New addition to the list sent from and used by the edit version screen
                    addNewAudioToList();
                }
                returnToPreviousScreen();
            }
        });

        // Back button is clicked
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (recordingPath == null)
                {
                    // Remove file if a new recording was made
                    File audioFile = new File(recordingOutput);
                    audioFile.delete();
                }

                returnToPreviousScreen();
            }
        });

        // ---------------------------------------------------- Audio handling
        // Record button is clicked
        recordBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!recordingInProgress)
                    startRecording();
                else
                    stopRecording();
            }
        });

        // Play button is clicked
        playBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                playRecording();
            }
        });

        // Stop button is clicked
        stopBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stopPlaying();
            }
        });

        // Reload button is clicked
        reloadBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Delete audio recording
                File audioFile = new File(recordingOutput);
                audioFile.delete();

                Intent i = new Intent(RecordOrPlayAudioActivity.this, RecordOrPlayAudioActivity.class);
                i = populateIntentData(i);
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });
    }

    private void startRecording()
    {
        if (recorder == null)
        {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // I used ChatGPT to explain how I can improve the audio quality as the standard quality was not good enough for a music app
            // <<<<<<< Start of Chat GPT aided code >>>>>>>>
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(recordingOutput);
            // Set a high quality sampling rate for great recording quality - 48 kHz
            recorder.setAudioSamplingRate(48000);
            // Set a high audio encoding bitrate for great recording quality - 192 kbps
            recorder.setAudioEncodingBitRate(192000);
            // <<<<<<< end of Chat GPT aided code >>>>>>>>
            try
            {
                recorder.prepare();
                recorder.start();
                recordingInProgress = true;
                audioFileCounterValue += 1;
                // Update UI header text
                headerTxt.setText(getString(R.string.end_recording_message));
            }
            catch (IOException e) {}
        }
    }

    private void stopRecording()
    {
        if (null != recorder)
        {
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingInProgress = false;
            // Update UI header text
            headerTxt.setText(getString(R.string.play_recording_message));
            // Remove record UI elements
            ((ViewGroup) recordLyt.getParent()).removeView(recordLyt);
            // Show needed UI elements
            playBtn.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.VISIBLE);
            reloadBtn.setVisibility(View.VISIBLE);
            recordBtn.setVisibility(View.VISIBLE);
        }
    }

    private void playRecording()
    {
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                // Change visibility of UI elements
                playBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);
            }
        });

        try
        {
            player.setDataSource(recordingOutput);
            player.prepare();
            player.start();
            // Hide play btn
            playBtn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.VISIBLE);
        }
        catch (IOException e) {}
    }

    private void stopPlaying()
    {
        if (null != player)
        {
            if (player.isPlaying())
                player.stop();
            player.release();
            player = null;
            // show play button again
            playBtn.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void returnToPreviousScreen()
    {
        Intent i;
        if (versionDescription == null)
        {
            // Previous screen was ViewVersion
            i = new Intent(RecordOrPlayAudioActivity.this, ViewVersionActivity.class);
            i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        }
        else if (wasPreviousScreenAddVersion)
        {
            // Previous screen was AddVersionData
            i = new Intent(RecordOrPlayAudioActivity.this, AddVersionActivity.class);
            i = populateIntentData(i);
        }
        else
        {
            // Previous screen was EditVersion
            i = new Intent(RecordOrPlayAudioActivity.this, EditVersionActivity.class);
            i = populateIntentData(i);
        }
        startActivity(i);
    }

    private void addNewAudioToList()
    {
        // Differentiate these new videos by saving their names
        String fullPathString = StringHelper.Audio_Folder_Path + "/";
        String currentFileName = recordingOutput.replace(fullPathString, "");
        // Add name of recording to list of newly created recordings
        listOfNewAudioNames.add(currentFileName);
    }

    private Intent populateIntentData(Intent i)
    {
        if (wasPreviousScreenAddVersion)
            i.putExtra(StringHelper.SongData_Intent_Name, songName);
        else
            i.putExtra(StringHelper.VersionData_Intent_ID, versionId);
        i.putExtra(StringHelper.VersionData_Intent_Name, versionName);
        i.putExtra(StringHelper.VersionData_Intent_Description, versionDescription);
        i.putExtra(StringHelper.VersionData_Intent_Lyrics, versionLyrics);
        i.putExtra(StringHelper.VersionData_Intent_View_Screen, true);
        i.putExtra(StringHelper.AudioData_Intent_Counter_Value, audioFileCounterValue);
        i.putExtra(StringHelper.AudioData_Intent_List_Of_New_Recordings, listOfNewAudioNames);
        return i;
    }
}