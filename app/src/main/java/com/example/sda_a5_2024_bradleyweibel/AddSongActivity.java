package com.example.sda_a5_2024_bradleyweibel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class AddSongActivity extends AppCompatActivity
{
    private EditText songNameEdt;
    private Button nextBtn, backToHomeBtn;
    private SharedPreferences songData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // Initializing all our variables
        songNameEdt = findViewById(R.id.idEdtSongName);
        nextBtn = findViewById(R.id.idBtnNext);
        backToHomeBtn = findViewById(R.id.idBtnBackToHome);

        // Get shared preferences
        songData = this.getSharedPreferences(StringHelper.SongData_SharedPreferences, Context.MODE_PRIVATE);

        // below line is to add on click listener for the next screen button
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
                // TODO: make sure no name in DB matches this one

                // Save name in SharedPreferences
                SharedPreferences.Editor editor = songData.edit();
                editor.putString(StringHelper.SongData_Preference_Name, songName);
                editor.apply();

                // Go to 'Add Version' page
                Intent i = new Intent(AddSongActivity.this, AddVersionActivity.class);
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
