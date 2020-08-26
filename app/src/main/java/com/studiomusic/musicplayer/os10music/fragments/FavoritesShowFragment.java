package com.studiomusic.musicplayer.os10music.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.activities.PlayingActivity;
import com.studiomusic.musicplayer.os10music.adapters.ListFavoritesAdapter;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.services.MusicService;
import com.studiomusic.musicplayer.os10music.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/11/2016.
 */
public class FavoritesShowFragment extends Fragment {
    private RecyclerView listFavoritesView;
    private ListFavoritesAdapter adapter;
    private ArrayList<SongObject> listFavorites;
    private TextView notFavoritesText;
    private MyApplication app;
    private DatabaseHelper db;
    private Gson gson;
    private SharedPreferences sharedPreferences;

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
        View rootView = inflater.inflate(R.layout.fragment_favorites_show, container, false);

        listFavoritesView = (RecyclerView) rootView.findViewById(R.id.list_show_favorites);
        notFavoritesText = (TextView) rootView.findViewById(R.id.empty_text);

        listFavorites = db.getAllFavoritesSongs();

        checkEmpty();

        adapter = new ListFavoritesAdapter(getActivity(), R.layout.item_favorite, listFavorites, new ListFavoritesAdapter.PositionClickListener() {
            @Override
            public void itemClicked(int position) {
                sharedPreferences.edit().putString("current song", gson.toJson(listFavorites.get(position))).apply();

                Intent i = new Intent(getActivity(), PlayingActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.enter_anim, R.anim.exit_anim);

                MusicService.setSong(listFavorites.get(position).getId());
                MusicService.playSong();
            }

            @Override
            public void deletedClicked(int position) {
                checkEmpty();
            }
        });
        adapter.setHasStableIds(true);

        listFavoritesView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        listFavoritesView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        listFavoritesView.setAdapter(adapter);

        return rootView;
    }

    private void checkEmpty() {
        if (listFavorites.size() > 0) {
            notFavoritesText.setVisibility(View.GONE);
        } else {
            notFavoritesText.setVisibility(View.VISIBLE);
        }
    }
}
