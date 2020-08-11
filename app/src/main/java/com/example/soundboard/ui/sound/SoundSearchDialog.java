/*
  Author: Ethan Darling
  Class: CS 3270
  SoundSearchDialog.java
 */

package com.example.soundboard.ui.sound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;

import com.example.soundboard.R;
import com.example.soundboard.db.AppDatabase;
import com.example.soundboard.db.Sound;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog that appears when searching for a sound.
 */
public class SoundSearchDialog extends DialogFragment {

    // Fields
    private List<Sound> soundList;
    private int soundCount = 0;

    private SoundFinder soundFinder = new SoundFinder();

    private OnSoundFragmentListener mCallBack;
    private static final String TAG = "SoundSearchDialog";

    View root;

    // Interface for when a sound is added.
    public interface OnSoundFragmentListener {
        void onAdd(int soundCount);
    }

    public SoundSearchDialog() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnSoundFragmentListener)
            mCallBack = (OnSoundFragmentListener) context;
        else
            throw new ClassCastException(context.toString() + "Must implement add sound fragment listener");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sound_search_dialog, container, false);

        soundList = new ArrayList<>();

        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View searchView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_sound_search_dialog, (ViewGroup) getView(), false);

        final TextInputEditText searchInput = searchView.findViewById(R.id.search_input);

        builder.setTitle("Search for a Sound by Title")
                .setView(searchView)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String searchString = String.valueOf(searchInput.getText());
                        String mediaType = MediaStore.Audio.Media.IS_MUSIC;

                        soundList = soundFinder.searchSounds(getActivity(), searchString, mediaType);
                        fillDatabase();

                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    /**
     * Fills the room database with sounds based off of the given search query.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fillDatabase() {
        soundCount = 0;
        new Thread(new Runnable() {
            LiveData<List<Sound>> sounds;
            AppDatabase database = AppDatabase.getInstance(getContext());

            @Override
            public void run() {
                database.clearAllTables();

                for (Sound s : soundList) {
                    Sound sound = new Sound(s.id, s.name, s.path, s.album, s.artist,
                            s.duration, s.size);
                    database.soundDAO().insert(sound);
                    Log.d(TAG, "fillDatabase: " + sound.toString());
                }
                Log.d(TAG, "------------------------------------------------");

                sounds = database.soundDAO().getAll();
                soundCount = database.soundDAO().getSoundCount();
                mCallBack.onAdd(soundCount);
            }
        }).start();
    }
}