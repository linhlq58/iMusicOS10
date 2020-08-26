package com.studiomusic.musicplayer.os10music.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;
import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.activities.PlayingActivity;
import com.studiomusic.musicplayer.os10music.adapters.ListSongsAdapter;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.services.MusicService;
import com.studiomusic.musicplayer.os10music.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class SongsFragment extends Fragment {
    private RecyclerView listSongsView;
    private ListSongsAdapter listSongsAdapter;
    private ArrayList<SongObject> listSongs;
    private EditText searchInput;
    private SharedPreferences sharedPreferences;
    private MyApplication app;
    private DatabaseHelper db;
    private Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getActivity().getApplication();
        db = app.getDatabase();
        gson = new Gson();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);

        listSongsView = (RecyclerView) rootView.findViewById(R.id.list_songs);
        searchInput = (EditText) rootView.findViewById(R.id.search_input);

        listSongs = db.getAllSongs();

        listSongsAdapter = new ListSongsAdapter(getActivity(), R.layout.item_song, listSongs, new ListSongsAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                sharedPreferences.edit().putString("current song", gson.toJson(listSongs.get(position))).apply();

                Intent i = new Intent(getActivity(), PlayingActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

                MusicService.setSong(listSongsAdapter.getItem(position).getId());
                MusicService.playSong();

            }
        });
        listSongsAdapter.setHasStableIds(true);

        listSongsView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listSongsView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        listSongsView.setAdapter(listSongsAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listSongsAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return rootView;
    }
}
