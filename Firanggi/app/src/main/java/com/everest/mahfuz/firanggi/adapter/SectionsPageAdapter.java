package com.everest.mahfuz.firanggi.adapter;

import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.everest.mahfuz.firanggi.fragments.AllUsersFragment;
import com.everest.mahfuz.firanggi.fragments.ChatFragment;
import com.everest.mahfuz.firanggi.fragments.FriendsFragment;
import com.everest.mahfuz.firanggi.fragments.RequestFragment;

public class SectionsPageAdapter extends FragmentPagerAdapter {
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return  chatFragment;
            case 1:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            case 2:
                RequestFragment requestFragment = new RequestFragment();
                return  requestFragment;
            case 3:
                AllUsersFragment usersFragment = new AllUsersFragment();
                return  usersFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Chats";
        }else if (position == 1) {
            return "Friends";
        }else if (position == 2) {
            return "Requests";
        }
        return "All";
    }
}
