package the.app.Lyricist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class ViewSongAndVersionsActivity extends AppCompatActivity
{
    private TextView songNameTxt;
    private FloatingActionButton editSongBtn, addVersionBtn, backToHomeBtn;
    private ArrayList<VersionModal> versionModalArrayList;
    private DBHandler dbHandler;
    private VersionRVAdapter versionRVAdapter;
    private RecyclerView versionsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_song_and_versions);

        // Getting song name from the adapter class
        String songName = getIntent().getStringExtra(StringHelper.SongData_Intent_Name);

        // Attaching variables to UI elements
        songNameTxt = findViewById(R.id.idTxtSongName);
        editSongBtn = findViewById(R.id.idBtnEditSong);
        addVersionBtn = findViewById(R.id.idBtnAddVersion);
        backToHomeBtn = findViewById(R.id.idBtnBackToHome);

        // Start preparing UI elements
        songNameTxt.setText(songName);

        // Initializing our all variables
        versionModalArrayList = new ArrayList<>();
        dbHandler = new DBHandler(ViewSongAndVersionsActivity.this);

        // Get id from SQLite DB
        Integer songId = dbHandler.getSongId(songName);

        // Getting song array list from db handler class
        versionModalArrayList = dbHandler.readVersions(songId);

        // Passing version array list to adapter class
        versionRVAdapter = new VersionRVAdapter(versionModalArrayList, ViewSongAndVersionsActivity.this);
        versionsRV = findViewById(R.id.idRVVersions);

        // Setting layout manager for recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewSongAndVersionsActivity.this, RecyclerView.VERTICAL, false);
        versionsRV.setLayoutManager(linearLayoutManager);

        // Setting adapter to recycler view
        versionsRV.setAdapter(versionRVAdapter);

        // Edit the song's name button is clicked
        editSongBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent
                Intent i = new Intent(ViewSongAndVersionsActivity.this, EditSongActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, songName);
                startActivity(i);
            }
        });

        // Add a new version button is clicked
        addVersionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent.
                Intent i = new Intent(ViewSongAndVersionsActivity.this, AddVersionActivity.class);
                // Pass song name through intent
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
                // Opening a new activity via a intent.
                Intent i = new Intent(ViewSongAndVersionsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
