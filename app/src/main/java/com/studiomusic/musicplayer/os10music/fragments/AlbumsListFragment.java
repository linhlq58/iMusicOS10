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
import com.studiomusic.musicplayer.os10music.adapters.ListAlbumsAdapter;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.AlbumObject;
import com.studiomusic.musicplayer.os10music.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/15/2016.
 */
public class AlbumsListFragment extends Fragment {
    private RecyclerView listAlbumsView;
    private ListAlbumsAdapter listAlbumsAdapter;
    private ArrayList<AlbumObject> listAlbums;
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
        View rootView = inflater.inflate(R.layout.fragment_albums_list, container, false);

        listAlbumsView = (RecyclerView) rootView.findViewById(R.id.list_albums);

        listAlbums = db.getAllAlbums();

        listAlbumsAdapter = new ListAlbumsAdapter(getActivity(), R.layout.item_album, listAlbums, new ListAlbumsAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                Intent i = new Intent("albums_item_clicked");
                i.putExtra("album_name", listAlbums.get(position).getName());
                i.putExtra("album_id", listAlbums.get(position).getId());
                getActivity().sendBroadcast(i);

            }
        });
        listAlbumsAdapter.setHasStableIds(true);

        listAlbumsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listAlbumsView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        listAlbumsView.setAdapter(listAlbumsAdapter);

        return rootView;
    }
}
