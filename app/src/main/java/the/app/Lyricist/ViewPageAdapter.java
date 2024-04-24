package the.app.Lyricist;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter
{
    private Context context;

    /**
     * Method to create the initial state for the adapter.
     *
     * @param fm passed fragment manager.
     * @param behavior passed int behaviour variable.
     * @param nContext passed context of the screen.
     */
    ViewPageAdapter(FragmentManager fm, int behavior, Context nContext)
    {
        super(fm, behavior);
        context = nContext;
    }

    /**
     * Method to populate the 2 fragment bodies and link them to the JAVA files.
     *
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = new Fragment();

        // Finds the tab position (note array starts at 0)
        position = position+1;

        // Finds the fragment
        switch (position)
        {
            case 1:
                fragment = new WelcomeFragment();
                break;
            case 2:
                fragment = new InstructionsFragment();
                break;
        }
        return fragment;
    }

    /**
     * @return returns the count of the fragments.
     */
    // Number of fragments
    @Override
    public int getCount()
    {
        return 2;
    }

    /**
     * Method returns the title of the fragment.
     *
     * @param position The position of the title requested
     * @return the name of the fragment.
     */
    @Override
    public CharSequence getPageTitle(int position)
    {
        position = position+1;
        CharSequence tabTitle = "";

        // Finds the fragment
        switch (position)
        {
            case 1:
                // Title of first fragment
                tabTitle = "Welcome";
                break;
            case 2:
                // Title of second fragment
                tabTitle = "Instructions";
                break;
        }
        return tabTitle;
    }
}
