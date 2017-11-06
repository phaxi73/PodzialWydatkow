package com.example.bartek.podzialwydatkow;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Bartek on 05.11.2017.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {   //pozycja każdej z zakładek

        switch(position){
            case 0:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            case 1:
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;

            case 2:
                InvitesFragment invitesFragment = new InvitesFragment();
                return invitesFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {       //liczba zakładek
        return 3;
    }

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "ZNAJOMI";

            case 1:
                return "GRUPY";

            case 2:
                return "ZAPROSZENIA";

            default:
                return null;
        }

    }
}
