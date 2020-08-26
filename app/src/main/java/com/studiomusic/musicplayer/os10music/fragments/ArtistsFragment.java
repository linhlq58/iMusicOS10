package com.studiomusic.musicplayer.os10music.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
public class ArtistsFragment extends Fragment {
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        changeFragment(new ArtistsListFragment());

        filter = new IntentFilter("artists_item_clicked");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                ArtistsDetailFragment artistsDetailFragment = new ArtistsDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("artist_name", extras.getString("artist_name"));
                bundle.putString("artist_id", extras.getString("artist_id"));
                artistsDetailFragment.setArguments(bundle);
                changeFragment(artistsDetailFragment);
            }
        };
        getActivity().registerReceiver(receiver, filter);

        return rootView;
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.artists_content, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void popBackStack() {
        getChildFragmentManager().popBackStack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }
}
