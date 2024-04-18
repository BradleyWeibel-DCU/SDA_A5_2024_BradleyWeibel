package com.example.sda_a5_2024_bradleyweibel;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
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
    public final static String VersionData_Intent_Description = "version_description";
    public final static String VersionData_Intent_Lyrics = "version_lyrics";
    public final static String VersionData_Intent_Add_Screen = "version_from_add_screen";
    public final static String VersionData_Intent_View_Screen = "version_from_media_viewer_screen";
    public final static String AudioData_Intent_Counter_Value = "audio_counter";
    public final static String AudioData_Intent_List_Of_New_Recordings = "audio_list_new_recordings";
    public final static String ImageData_Intent_Path = "image_path";
    public final static String VideoData_Intent_Path = "video_path";
    public final static String AudioData_Intent_Path = "audio_path";

    // Directories
    // TODO maybe use dynamic folder finding?
    public static String Image_Folder_Path = "/storage/emulated/0/Android/data/com.example.sda_a5_2024_bradleyweibel/files/Pictures";
    public static String Video_Folder_Path = "/storage/emulated/0/Android/data/com.example.sda_a5_2024_bradleyweibel/files/Movies";
    public static String Audio_Folder_Path = "/storage/emulated/0/Android/data/com.example.sda_a5_2024_bradleyweibel/files/Music";

    // Names
    public static String Image_Prefix = "IMG_";
    public static String Video_Prefix = "VID_";
    public static String Audio_Prefix = "REC_";
    public static String Image_Suffix_With_Dot = ".jpg";
    public static String Video_Suffix_With_Dot = ".mp4";
    public static String Audio_Suffix_With_Dot = ".acc";
    public static String Placeholder_Version_Name = "-PlaceHolderForNewVersionName-";

    // Authorities
    public final static String App_Authority = "com.example.sda_a5_2024_bradleyweibel";

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

    // Get file extension of image/video - used for saving
    public static String getFileExtension(Uri fileContentUri, ContentResolver c)
    {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(fileContentUri));
    }
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