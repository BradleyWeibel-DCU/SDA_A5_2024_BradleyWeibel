package com.example.sda_a5_2024_bradleyweibel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddSongActivity extends AppCompatActivity
{
    private EditText songNameEdt;
    private FloatingActionButton nextBtn, backToHomeBtn;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // Initializing all our variables
        songNameEdt = findViewById(R.id.idEdtSongName);
        nextBtn = findViewById(R.id.idBtnNext);
        backToHomeBtn = findViewById(R.id.idBtnBackToHome);

        // Initiate DB handler
        dbHandler = new DBHandler(AddSongActivity.this);

        // Get song name from intent if it was passed
        String songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);
        songNameEdt.setText(songName);

        // On click listener for the next screen button
        nextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Get song name from edit text field
                String songName = songNameEdt.getText().toString().trim();

                // validating if the text fields are empty or not.
                if (songName.isEmpty())
                {
                    // TODO: hard coded string
                    StringHelper.showToast("Please enter a name for the song", AddSongActivity.this);
                    return;
                }
                else if (!dbHandler.isSongNameUnique(songName))
                {
                    // TODO: hard coded string
                    StringHelper.showToast("Please enter a unique song name", AddSongActivity.this);
                    return;
                }

                // Go to 'Add Version' page
                Intent i = new Intent(AddSongActivity.this, AddVersionActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });

        // Back to home
        backToHomeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(AddSongActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
