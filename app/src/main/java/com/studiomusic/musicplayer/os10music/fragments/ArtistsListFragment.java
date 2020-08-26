package com.studiomusic.musicplayer.os10music.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.adapters.ListArtistsAdapter;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.ArtistObject;
import com.studiomusic.musicplayer.os10music.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/15/2016.
 */
public class ArtistsListFragment extends Fragment {
    private RecyclerView listArtistsView;
    private ListArtistsAdapter listArtistsAdapter;
    private ArrayList<ArtistObject> listArtists;
    private MyApplication app;
    private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getActivity().getApplication();
        db = app.getDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists_list, container, false);

        listArtistsView = (RecyclerView) rootView.findViewById(R.id.list_artists);

        listArtists = db.getAllArtists();

        listArtistsAdapter = new ListArtistsAdapter(getActivity(), R.layout.item_artist, listArtists, new ListArtistsAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                Intent i = new Intent("artists_item_clicked");
                i.putExtra("artist_name", listArtists.get(position).getName());
                i.putExtra("artist_id", listArtists.get(position).getId());
                getActivity().sendBroadcast(i);
            }
        });
        listArtistsAdapter.setHasStableIds(true);

        listArtistsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listArtistsView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        listArtistsView.setAdapter(listArtistsAdapter);

        return rootView;
    }
}
