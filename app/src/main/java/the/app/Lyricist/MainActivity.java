package the.app.Lyricist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    // Variables for UI elements
    private RecyclerView songsRV;
    private EditText searchBarEdt;
    private TextView headerTxt;
    private ImageView headerImg;
    private FloatingActionButton addSongBtn;

    // General variables
    private ArrayList<SongModal> songModalArrayList;
    private DBHandler dbHandler;
    private SongRVAdapter songRVAdapter;

    /**
     * Executes on screen load.
     * Creates the list of songs.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Attaching local variables to UI elements
        headerImg = findViewById(R.id.idImgLyricist);
        headerTxt = findViewById(R.id.idTxtLyricistHeader);
        addSongBtn = findViewById(R.id.idBtnAddSong);
        searchBarEdt = findViewById(R.id.idEdtSearchSongs);

        // Initializing variables
        songModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(MainActivity.this);

        // Getting song array list from DB handler class
        songModalArrayList = dbHandler.readSongs();

        // Passing song array list to adapter class
        songRVAdapter = new SongRVAdapter(songModalArrayList, MainActivity.this);
        songsRV = findViewById(R.id.idRVSongs);

        // Setting layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        songsRV.setLayoutManager(linearLayoutManager);

        // Setting adapter to recycler view in UI
        songsRV.setAdapter(songRVAdapter);

        // User has clicked the Lyricist Image, open help screen
        headerImg.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Executes on click of app icon.
             * Opens 'help' screen.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via an intent
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i);
            }
        });

        // User has clicked the Lyricist header, open help screen
        headerTxt.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Executes on click of app header.
             * Opens 'help' screen.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via an intent
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(i);
            }
        });

        // Add new song button is pushed
        addSongBtn.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Executes after click of 'add song' button.
             * Opens add song screen.
             *
             * @param v The view that was clicked.
             */
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
            /**
             * Empty constructor, executes before the text in the search bar has been changed.
             *
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {} // Do not remove

            /**
             * Executes when text has been changed.
             * Searches in song table in DB for matches like what has been entered in search bar.
             *
             * @param s
             * @param start
             * @param before
             * @param count
             */
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
                    searchBarEdt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_hollow_icon_orange, 0, 0, 0);
                else
                    searchBarEdt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search_full_icon_orange, 0, 0, 0);
            }

            /**
             * Empty constructor, executes after text has been changed in search bar.
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {} // Do not remove
        });
    }
}