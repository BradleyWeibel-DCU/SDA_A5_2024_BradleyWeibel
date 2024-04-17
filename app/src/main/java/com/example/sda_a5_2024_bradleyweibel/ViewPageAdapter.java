package com.example.sda_a5_2024_bradleyweibel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPageAdapter extends FragmentPagerAdapter
{

    private Context context;

    ViewPageAdapter(FragmentManager fm, int behavior, Context nContext)
    {
        super(fm, behavior);
        context = nContext;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {

        Fragment fragment = new Fragment();

        //finds the tab position (note array starts at 0)
        position = position+1;

        //finds the fragment
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

    @Override
    public int getCount()
    {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        position = position+1;
        CharSequence tabTitle = "";

        //finds the fragment
        switch (position)
        {
            case 1:
                //code
                tabTitle = "Welcome";
                break;
            case 2:
                //code
                tabTitle = "Instructions";
                break;
        }
        return tabTitle;
    }
}
