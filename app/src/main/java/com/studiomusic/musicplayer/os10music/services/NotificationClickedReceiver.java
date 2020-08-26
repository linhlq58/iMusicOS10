package com.studiomusic.musicplayer.os10music.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Linh Lee on 11/14/2016.
 */
public class NotificationClickedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        int command = extras.getInt("command");
        switch (command) {
            case 1:
                Intent closeIntent = new Intent("noti_control");
                closeIntent.putExtra("command", "close");
                context.sendBroadcast(closeIntent);
                break;
            case 2:
                if (MusicService.isPlaying()) {
                    MusicService.onPause();
                } else {
                    MusicService.onResume();
                }
                Intent controlIntent = new Intent("noti_control");
                controlIntent.putExtra("command", "play_pause");
                context.sendBroadcast(controlIntent);
                break;
            case 3:
                MusicService.playNext();
                break;
        }
    }
}
