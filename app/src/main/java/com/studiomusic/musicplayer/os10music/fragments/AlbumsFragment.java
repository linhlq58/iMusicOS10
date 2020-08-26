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
public class AlbumsFragment extends Fragment {
    private BroadcastReceiver receiver;
    private IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);

        changeFragment(new AlbumsListFragment());

        filter = new IntentFilter("albums_item_clicked");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();

                AlbumsDetailFragment albumsDetailFragment = new AlbumsDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("album_name", extras.getString("album_name"));
                bundle.putString("album_id", extras.getString("album_id"));
                albumsDetailFragment.setArguments(bundle);
                changeFragment(albumsDetailFragment);
            }
        };
        getActivity().registerReceiver(receiver, filter);

        return rootView;
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.albums_content, fragment);
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
