package com.studiomusic.musicplayer.os10music.adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.objects.SongObject;
import com.studiomusic.musicplayer.os10music.utils.RemoveAccent;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/7/2016.
 */
public class ListSongsAdapter extends RecyclerView.Adapter<ListSongsAdapter.RecyclerViewHolder> implements Filterable {
    private Activity context;
    private int layout;
    private ArrayList<SongObject> listSongsOriginal;
    private ArrayList<SongObject> listSongsDisplayed;

    private int selectedPos = 0;
    private PositionClickListener listener;

    public ListSongsAdapter(Activity context, int layout, ArrayList<SongObject> listSongs, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listSongsOriginal = listSongs;
        this.listSongsDisplayed = listSongs;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Glide.with(context).load(Uri.parse(listSongsDisplayed.get(position).getAlbumArtUri())).placeholder(R.mipmap.ic_album_default).error(R.mipmap.ic_album_default)
                .crossFade().centerCrop().into(holder.albumArt);

        holder.songName.setText(listSongsDisplayed.get(position).getName());
        holder.songArtist.setText(listSongsDisplayed.get(position).getArtist());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listSongsDisplayed.size();
    }

    public SongObject getItem(int position) {
        return listSongsDisplayed.get(position);
    }

    public interface PositionClickListener {
        void itemClicked(int position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundedImageView albumArt;
        public TextView songName;
        public TextView songArtist;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            albumArt = (RoundedImageView) itemView.findViewById(R.id.album_art);
            songName = (TextView) itemView.findViewById(R.id.song_name);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);

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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<SongObject> filteredArrList = new ArrayList<>();

                if (constraint == null) {
                    filteredArrList.addAll(listSongsOriginal);
                } else {
                    constraint = RemoveAccent.removeAccent(constraint.toString().toLowerCase());
                    for (int i = 0; i < listSongsOriginal.size(); i++) {
                        String songName = listSongsOriginal.get(i).getName();
                        songName = RemoveAccent.removeAccent(songName.toString().toLowerCase());
                        if (songName.indexOf(constraint.toString()) > -1) {
                            filteredArrList.add(listSongsOriginal.get(i));
                        }
                    }
                }

                results.values = filteredArrList;
                results.count = filteredArrList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listSongsDisplayed = (ArrayList<SongObject>) results.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
