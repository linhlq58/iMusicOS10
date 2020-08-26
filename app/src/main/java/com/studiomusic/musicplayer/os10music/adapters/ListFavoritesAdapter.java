package com.studiomusic.musicplayer.os10music.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.studiomusic.musicplayer.os10music.MyApplication;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.database.DatabaseHelper;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.utils.Constant;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/11/2016.
 */
public class ListFavoritesAdapter extends RecyclerView.Adapter<ListFavoritesAdapter.RecyclerViewHolder> {
    private Activity context;
    private int layout;
    private ArrayList<SongObject> listFavorites;

    private int selectedPos = 0;
    private PositionClickListener listener;

    public ListFavoritesAdapter(Activity context, int layout, ArrayList<SongObject> listFavorites, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listFavorites = listFavorites;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.songName.setText(listFavorites.get(position).getName());
        holder.songArtist.setText(listFavorites.get(position).getArtist());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listFavorites.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface PositionClickListener {
        void itemClicked(int position);
        void deletedClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public MyApplication app;
        public DatabaseHelper db;
        public TextView songName;
        public TextView songArtist;
        public ImageView deleteButton;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            app = (MyApplication) context.getApplication();
            db = app.getDatabase();
            songName = (TextView) itemView.findViewById(R.id.song_name);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            deleteButton = (ImageView) itemView.findViewById(R.id.delete_favorite);

            itemView.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            Constant.increaseHitArea(deleteButton);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.delete_favorite:
                    Toast.makeText(context, listFavorites.get(getLayoutPosition()).getName() + " is deleted!", Toast.LENGTH_SHORT).show();
                    db.deleteFavoriteSong(listFavorites.get(getLayoutPosition()).getId());
                    listFavorites.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    notifyItemRangeChanged(getLayoutPosition(), listFavorites.size());

                    listener.deletedClicked(getLayoutPosition());
                    break;
                default:
                    listener.itemClicked(getLayoutPosition());
                    notifyItemChanged(selectedPos);
                    selectedPos = getLayoutPosition();
                    notifyItemChanged(selectedPos);
                    break;
            }
        }
    }
}
