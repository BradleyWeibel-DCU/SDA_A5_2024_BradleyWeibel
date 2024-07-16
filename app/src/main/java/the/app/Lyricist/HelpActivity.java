package the.app.Lyricist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class HelpActivity extends AppCompatActivity
{
    private FloatingActionButton backToHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Initializing all our variables
        backToHomeBtn = findViewById(R.id.idBtnBackToHome);

        // Initialize two fragments in body of screen
        ViewPager viewPager = findViewById(R.id.idHelpPager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), NumberHelper.Behaviour_Resume_Current_Tab, getApplicationContext());
        viewPager.setAdapter(adapter);
        // Initialize tab headers
        TabLayout tabLayout = findViewById(R.id.idHelpTabHeaders);
        tabLayout.setupWithViewPager(viewPager);

        // Back to home button clicked
        backToHomeBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // opening a new activity via a intent.
                Intent i = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}

