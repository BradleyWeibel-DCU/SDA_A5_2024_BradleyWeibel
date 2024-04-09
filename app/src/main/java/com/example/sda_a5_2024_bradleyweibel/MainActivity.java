package com.example.sda_a5_2024_bradleyweibel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<SongModal> songModalArrayList;
    private DBHandler dbHandler;
    private SongRVAdapter songRVAdapter;
    private RecyclerView songsRV;
    private Button addSongButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attaching the button to the variable
        addSongButton = findViewById(R.id.idBtnAddSong);

        // Initializing our all variables
        songModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(MainActivity.this);

        // getting our song array list from db handler class
        songModalArrayList = dbHandler.readSongs();

        // Passing our song array list to our adapter class
        songRVAdapter = new SongRVAdapter(songModalArrayList, MainActivity.this);
        songsRV = findViewById(R.id.idRVSongs);

        // Setting layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        songsRV.setLayoutManager(linearLayoutManager);

        // Setting adapter to recycler view
        songsRV.setAdapter(songRVAdapter);

        // Add new song button is pushed
        addSongButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via an intent
                Intent i = new Intent(MainActivity.this, AddSongActivity.class);
                startActivity(i);
            }
        });
    }
}