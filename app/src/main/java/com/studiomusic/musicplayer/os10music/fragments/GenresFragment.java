package com.studiomusic.musicplayer.os10music.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.studiomusic.musicplayer.os10music.R;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class GenresFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout unknownLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_genres, container, false);

        unknownLayout = (RelativeLayout) rootView.findViewById(R.id.unknown_layout);
        unknownLayout.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent("control_tabs");
        i.putExtra("tab_id", 0);
        getActivity().sendBroadcast(i);
    }
}
