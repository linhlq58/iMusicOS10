package com.studiomusic.musicplayer.os10music.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Linh Lee on 11/8/2016.
 */
public class VolumeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int volumeLevel= am.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d("test 1", "okkk");

        Intent i = new Intent("change seek bar");
        i.putExtra("volume level", volumeLevel);
        context.sendBroadcast(i);
    }
}
