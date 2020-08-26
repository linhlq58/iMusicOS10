package com.studiomusic.musicplayer.os10music.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studiomusic.musicplayer.os10music.R;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class FavoritesFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        changeFragment(new FavoritesShowFragment());

        return rootView;
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.favorite_content, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
