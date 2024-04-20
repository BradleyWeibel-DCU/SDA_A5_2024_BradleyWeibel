package the.app.Lyricist;

import android.content.res.Resources;
import android.util.TypedValue;
import java.util.concurrent.ThreadLocalRandom;

public class NumberHelper
{
    // Static keys for image/video/audio handling
    public static final int REQUEST_CODE = 100;
    public static final int REQUEST_CHOOSE_PHOTO = 101;
    public static final int REQUEST_TAKE_PHOTO = 102;
    public static final int REQUEST_CHOOSE_VIDEO = 103;
    public static final int REQUEST_TAKE_VIDEO = 104;
    public static final int REQUEST_CHOOSE_RECORDING = 105;

    // UI element values
    // Images
    public static final int Image_Margin_Top = 10;
    public static final int Image_Margin_Right = 0;
    public static final int Image_Margin_Bottom = 10;
    public static final int Image_Margin_Left = 0;
    // Videos
    public static final int Video_Get_Frame_At_1_Second = 1000000;
    public static final int Video_Thumbnail_Height = 130;
    public static final int Video_Margin_Top = 10;
    public static final int Video_Margin_Right = 0;
    public static final int Video_Margin_Bottom = 10;
    public static final int Video_Margin_Left = 0;

    // General
    public static final int Behaviour_Resume_Current_Tab = 1;

    // Get 130dp in pixels for imageView size in UI
    public static int imageViewDPSizeInPX(Resources r)
    {
        float dip = 130f;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        int result = Math.round(px);
        return result;
    }

    public static Boolean isNumberEven(int number)
    {
        return number% 2 == 0;
    }

    public static String randomNumberGenerator()
    {
        // Define the range
        int max = 1000000000;
        int min = 1;
        // Generate random numbers within 1 to 1 000 000 000
        return String.valueOf(ThreadLocalRandom.current().nextInt(min, max + 1));
    }
}