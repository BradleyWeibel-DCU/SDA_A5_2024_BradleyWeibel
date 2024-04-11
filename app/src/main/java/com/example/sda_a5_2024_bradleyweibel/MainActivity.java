package com.example.sda_a5_2024_bradleyweibel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<SongModal> songModalArrayList;
    private DBHandler dbHandler;
    private SongRVAdapter songRVAdapter;
    private RecyclerView songsRV;
    private EditText searchBarEdt;
    private FloatingActionButton addSongBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attaching local variables to UI elements
        addSongBtn = findViewById(R.id.idBtnAddSong);
        searchBarEdt = findViewById(R.id.idEdtSearchSongs);

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
        addSongBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via an intent
                Intent i = new Intent(MainActivity.this, AddSongActivity.class);
                startActivity(i);
            }
        });

        // Text has been typed into the search bar
        searchBarEdt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {} // Do not remove

            // Where the magic happens
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // Get new search term
                String songSearchTerm = searchBarEdt.getText().toString().trim();
                // Search new search term against song names
                songModalArrayList = dbHandler.readSongs(songSearchTerm);

                // Populate the UI with the song list as is done above...
                songRVAdapter = new SongRVAdapter(songModalArrayList, MainActivity.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                songsRV.setLayoutManager(linearLayoutManager);
                songsRV.setAdapter(songRVAdapter);

                // Set searching image in search bar
                if (songSearchTerm.equals(""))
                    searchBarEdt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_icon, 0, 0, 0);
                else
                    searchBarEdt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.searching_icon, 0, 0, 0);
            }

            @Override
            public void afterTextChanged(Editable s) {} // Do not remove
        });
    }
}