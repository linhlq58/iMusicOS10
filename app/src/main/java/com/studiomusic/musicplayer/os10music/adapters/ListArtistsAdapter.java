package com.studiomusic.musicplayer.os10music.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.objects.ArtistObject;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/14/2016.
 */
public class ListArtistsAdapter extends RecyclerView.Adapter<ListArtistsAdapter.RecyclerViewHolder> {
    private Activity context;
    private int layout;
    private ArrayList<ArtistObject> listArtistsOriginal;
    private ArrayList<ArtistObject> listArtistsDisplayed;

    private int selectedPos = 0;
    private PositionClickListener listener;

    public ListArtistsAdapter(Activity context, int layout, ArrayList<ArtistObject> listArtists, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listArtistsOriginal = listArtists;
        this.listArtistsDisplayed = listArtists;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.artistName.setText(listArtistsDisplayed.get(position).getName());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listArtistsDisplayed.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface PositionClickListener {
        void itemClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView artistName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.itemClicked(getLayoutPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
}
