package the.app.Lyricist;

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

    /**
     * Executes on initial load for this screen and sets up screen for adding a new song.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        // Initializing variables
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
            /**
             * Triggered on click of the 'next' button.
             * Validates text of Sing name and proceeds to next screen if conditions are met.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                // Get song name from edit text field
                String songName = songNameEdt.getText().toString().trim();

                // Validating if the text fields are empty or not
                if (songName.isEmpty())
                {
                    StringHelper.showToast(getString(R.string.toastr_missing_song_name), AddSongActivity.this);
                    return;
                }
                else if (!dbHandler.isSongNameUnique(songName))
                {
                    StringHelper.showToast(getString(R.string.toastr_unique_song_name), AddSongActivity.this);
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
            /**
             * Triggered on click to 'return to home' button.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                // Opening a new activity via a intent.
                Intent i = new Intent(AddSongActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
