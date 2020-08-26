package com.studiomusic.musicplayer.os10music.fragments;

import android.os.Bundle;
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
import android.widget.Toast;

import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.adapters.ListSongsAdapter;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/11/2016.
 */
public class FavoritesAddFragment extends Fragment {
    private RecyclerView listSongsView;
    private ListSongsAdapter listSongsAdapter;
    private ArrayList<SongObject> listSongs;
    private EditText searchInput;
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
        View rootView = inflater.inflate(R.layout.fragment_favorites_add, container, false);

        listSongsView = (RecyclerView) rootView.findViewById(R.id.list_songs);
        searchInput = (EditText) rootView.findViewById(R.id.search_input);

        listSongs = db.getAllSongs();

        listSongsAdapter = new ListSongsAdapter(getActivity(), R.layout.item_song, listSongs, new ListSongsAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                db.addFavoriteSong(listSongs.get(position).getId());
                Toast.makeText(getActivity(), listSongs.get(position).getName() + " is added!", Toast.LENGTH_SHORT).show();
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
