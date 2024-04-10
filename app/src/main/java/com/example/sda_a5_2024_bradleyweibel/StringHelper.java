package com.example.sda_a5_2024_bradleyweibel;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringHelper
{
    // SharedPreference names
    public final static String SongData_SharedPreferences = "SongDataPreferences";
    // SharedPreference key names
    public final static String SongData_Preference_Name = "song_name";

    // Intent data values
    public final static String SongData_Intent_ID = "song_id";
    public final static String SongData_Intent_Name = "song_name";
    public final static String VersionData_Intent_ID = "version_id";
    public final static String VersionData_Intent_Name = "version_name";

    // Show a customized toast message
    public static void showToast(String message, Context context)
    {
        // Create toast object
        Toast toastr = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toastr.getView();
        // Make changes to colour appearance
        view.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.color_toast), PorterDuff.Mode.SRC_IN);
        // Insert message
        TextView text = view.findViewById(android.R.id.message);
        // Set text colour
        text.setTextColor(ContextCompat.getColor(context, R.color.color_toast_text));
        // Show toast
        toastr.show();
    }

    // Get current date in '09 Apr 2024' format
    public static String getFormattedDate() { return new SimpleDateFormat("dd MMM yyyy").format(new Date()); }
}

// TODO: Shared preference code, maybe it is needed later
// private SharedPreferences songData;

// Get shared preferences
//songData = this.getSharedPreferences(StringHelper.SongData_SharedPreferences, Context.MODE_PRIVATE);
// Set song name in UI
//songNameTxt.setText(songData.getString(StringHelper.SongData_Preference_Name, ""));

// Clear or insert song name into shared preferences
//SharedPreferences.Editor editor = songData.edit();
//editor.putString(StringHelper.SongData_Preference_Name, "");
//editor.apply();