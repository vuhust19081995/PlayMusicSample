package com.workspace.playmusicsample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.workspace.playmusicsample.R;
import com.workspace.playmusicsample.data.model.Song;

import java.util.ArrayList;

/**
 * Created by workspace on 06/09/2017.
 */

public class SongAdapter extends BaseAdapter {
    private ArrayList<Song> listSong;
    private LayoutInflater layoutInflater;

    public SongAdapter(ArrayList<Song> listSong, LayoutInflater layoutInflater) {
        this.listSong = listSong;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return listSong.size();
    }

    @Override
    public Song getItem(int position) {
        return listSong.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View viewSong = layoutInflater.inflate(R.layout.item_song,null);
        TextView songView = (TextView)viewSong.findViewById(R.id.song_title);
        TextView artistView = (TextView)viewSong.findViewById(R.id.song_artist);
        Song currSong = listSong.get(position);
        songView.setText(currSong.getTitle());
//        artistView.setText(currSong.getArtist());
        viewSong.setTag(position);
        return viewSong;
    }
}
