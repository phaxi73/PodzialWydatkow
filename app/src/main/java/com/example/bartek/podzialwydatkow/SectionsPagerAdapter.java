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
                ExpensesFragment expensesFragment = new ExpensesFragment();
                return expensesFragment;

            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

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
    }       //Ilość zakładek

    public CharSequence getPageTitle(int position){

        switch (position){

            case 0:
                return "WYDATKI";

            case 1:
                return "ZNAJOMI";

            case 2:
                return "ZAPROSZENIA";

            default:
                return null;
        }

    }
}
