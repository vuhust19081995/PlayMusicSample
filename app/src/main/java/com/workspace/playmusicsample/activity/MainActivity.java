package com.workspace.playmusicsample.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

import com.workspace.playmusicsample.R;
import com.workspace.playmusicsample.adapter.SongAdapter;
import com.workspace.playmusicsample.data.local.MusicController;
import com.workspace.playmusicsample.data.local.MusicService;
import com.workspace.playmusicsample.data.model.Song;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity{
    private ArrayList<Song> songList;
    private ListView lvSongList;
    private SongAdapter songAdapter;
    private MusicService musicService;
    private boolean musicBound = false;
    private Intent playIntent;
    private MusicController controller;
    private boolean paused=false, playbackPaused=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvSongList = (ListView) findViewById(R.id.lvSongList);
        songList = new ArrayList<Song>();
        getSongListFromRaw();
        shortListSong();
        songAdapter = new SongAdapter(songList,getLayoutInflater());
        lvSongList.setAdapter(songAdapter);
        setController();
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
    }

    private void setController(){
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        MediaController.MediaPlayerControl mediaPlayerControl = new MediaController.MediaPlayerControl() {
            @Override
            public void start() {
                musicService.go();
            }

            @Override
            public void pause() {
                playbackPaused=true;
                musicService.pausePlayer();
            }

            @Override
            public int getDuration() {
                if(musicService!=null && musicBound&& musicService.isPng())
                return musicService.getDur();
  else return 0;
            }

            @Override
            public int getCurrentPosition() {
                if(musicService!=null && musicBound && musicService.isPng())
                return musicService.getPosn();
  else return 0;
            }

            @Override
            public void seekTo(int position) {
                musicService.seek(position);
            }

            @Override
            public boolean isPlaying() {
                if(musicService!=null && musicBound)
                return musicService.isPng();
                return false;
            }

            @Override
            public int getBufferPercentage() {
                return 0;
            }

            @Override
            public boolean canPause() {
                return true;
            }

            @Override
            public boolean canSeekBackward() {
                return true;
            }

            @Override
            public boolean canSeekForward() {
                return true;
            }

            @Override
            public int getAudioSessionId() {
                return 0;
            }
        };
        controller.setMediaPlayer(mediaPlayerControl);
        controller.setAnchorView(findViewById(R.id.lvSongList));
        controller.setEnabled(true);
    }

    //play next
    private void playNext(){
        musicService.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    //play previous
    private void playPrev(){
        musicService.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    public void getSongListFromRaw(){
        songList = new ArrayList<>();
        try
        {
            Field fields[] = R.raw.class.getDeclaredFields() ;

            for( int i=0; i< fields.length; i++ ) {
                Field field = fields[i] ;
                songList.add(new Song(field.getName()));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shortListSong(){
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
    }

    public void songPicked(View view){
        musicService.setSong(Integer.parseInt(view.getTag().toString()));
        musicService.playSong();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicService.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicService=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService=null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }
}
