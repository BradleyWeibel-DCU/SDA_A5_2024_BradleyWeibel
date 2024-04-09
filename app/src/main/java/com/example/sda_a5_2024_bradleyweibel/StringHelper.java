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
    // Song data
    public final static String SongData_Preference_Name = "name";

    // Show a customized toast message
    public static void showToast(String message, Context context)
    {
        // Create toast object
        Toast toastr = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toastr.getView();
        // Make changes to colour appearance
        //view.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorToast), PorterDuff.Mode.SRC_IN);
        // Insert message
        TextView text = view.findViewById(android.R.id.message);
        // Set text colour
        //text.setTextColor(ContextCompat.getColor(context, R.color.colorToastText));
        // Show toast
        toastr.show();
    }

    // Get current date in '09 Apr 2024' format
    public static String getFormattedDate() { return new SimpleDateFormat("dd MMM yyyy").format(new Date()); }
}
