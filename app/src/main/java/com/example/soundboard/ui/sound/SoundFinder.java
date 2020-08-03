package com.example.soundboard.ui.sound;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.soundboard.db.Sound;

import java.util.ArrayList;
import java.util.List;

public class SoundFinder extends Fragment {

    private List<Sound> soundList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<Sound> getSounds(Activity activity, String filePath, String mediaType) {
        ContentResolver soundResolver = activity.getContentResolver();
        String selection = mediaType + " != 0 AND " + MediaStore.Audio.Media.DATA + " LIKE '" + filePath + "/%'";
        Uri soundUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor soundCursor = soundResolver.query(soundUri, null, selection, null, " ARTIST asc");

        cursorLoop(soundCursor);
        return soundList;
    }

    public List<Sound> searchSounds(Activity activity, String searchString, String mediaType) {
        ContentResolver soundResolver = activity.getContentResolver();
        String selection = mediaType + " != 0 AND " + MediaStore.Audio.Media.TITLE + " LIKE '" + "%" + searchString + "%'";
        Uri soundUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor soundCursor = soundResolver.query(soundUri, null, selection, null, " TITLE asc");

        cursorLoop(soundCursor);
        return soundList;
    }

    private void cursorLoop(Cursor soundCursor) {
        if (soundCursor != null && soundCursor.moveToFirst()) {
            int id = soundCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int name = soundCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int path = soundCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int duration = soundCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int album = soundCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artist = soundCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int size = soundCursor.getColumnIndex(MediaStore.Audio.Media.SIZE);

            do {
                String soundTitle = soundCursor.getString(id);
                long soundId = soundCursor.getLong(name);
                String soundPath = soundCursor.getString(path);
                int soundDuration = soundCursor.getInt(duration);
                String soundAlbum = soundCursor.getString(album);
                String soundArtist = soundCursor.getString(artist);
                int soundSize = soundCursor.getInt(size);

                Sound sound = new Sound(String.valueOf(soundId), soundTitle, soundPath, soundAlbum,
                        soundArtist, soundDuration, soundSize);
                soundList.add(sound);
            }
            while (soundCursor.moveToNext());
            soundCursor.close();
        }
    }
}
