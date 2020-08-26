package com.studiomusic.musicplayer.os10music.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studiomusic.musicplayer.os10music.R;
import com.studiomusic.musicplayer.os10music.objects.AlbumObject;

import java.util.ArrayList;

/**
 * Created by Linh Lee on 11/14/2016.
 */
public class ListAlbumsAdapter extends RecyclerView.Adapter<ListAlbumsAdapter.RecyclerViewHolder> {
    private Activity context;
    private int layout;
    private ArrayList<AlbumObject> listAlbums;

    private int selectedPos = 0;
    private PositionClickListener listener;

    public ListAlbumsAdapter(Activity context, int layout, ArrayList<AlbumObject> listAlbums, PositionClickListener listener) {
        this.context = context;
        this.layout = layout;
        this.listAlbums = listAlbums;
        this.listener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = context.getLayoutInflater().inflate(layout, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.albumImg.setImageResource(R.mipmap.ic_album_default);
        holder.albumName.setText(listAlbums.get(position).getName());

        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return listAlbums.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface PositionClickListener {
        void itemClicked(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView albumImg;
        public TextView albumName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            albumImg = (ImageView) itemView.findViewById(R.id.album_image);
            albumName = (TextView) itemView.findViewById(R.id.album_name);

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
