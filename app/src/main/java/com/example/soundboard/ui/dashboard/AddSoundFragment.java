package com.example.soundboard.ui.dashboard;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.soundboard.R;
import com.example.soundboard.db.AppDatabase;
import com.example.soundboard.db.Sound;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddSoundFragment extends Fragment {

    private AddSoundViewModel addSoundViewModel;
    private static final String TAG = "AddSoundFragment";
    private List<Sound> soundList;
    private TextInputEditText sId, sName;
    private String sPath;
    private OnSoundFragmentListener mCallBack;
    View root;
    private String filePath, mediaType;
    private ProgressBar progressBar;

    public interface OnSoundFragmentListener {
        void onAdd();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnSoundFragmentListener)
            mCallBack = (OnSoundFragmentListener) context;
        else
            throw new ClassCastException(context.toString() + "Must implement add sound fragment listener");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addSoundViewModel = ViewModelProviders.of(this).get(AddSoundViewModel.class);
        root = inflater.inflate(R.layout.fragment_add_sound, container, false);
        final TextView textView = root.findViewById(R.id.text_addSound);
        addSoundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

//        sId = root.findViewById(R.id.text_sound_id);
//        sName = root.findViewById(R.id.text_sound_name);
//        sPath = "Test";

        soundList = new ArrayList<>();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar = root.findViewById(R.id.progress_bar);

        Button btnDownloads = root.findViewById(R.id.button_downloads);
        Button btnNotifications = root.findViewById(R.id.button_notifications);
        Button btnAll = root.findViewById(R.id.button_all);

        View.OnClickListener btnListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                int btn = view.getId();

                switch (btn) {
                    case R.id.button_downloads:
                        filePath = Environment.getExternalStorageDirectory().getPath() + "/Download";
                        mediaType = MediaStore.Audio.Media.IS_MUSIC;
                        getSounds(filePath, mediaType);
                        break;
                    case R.id.button_notifications:
                        filePath = Environment.getExternalStorageDirectory().getPath();
                        mediaType = MediaStore.Audio.Media.IS_NOTIFICATION;
                        getSounds(filePath, mediaType);
                        break;
                    case R.id.button_all:
                        filePath = Environment.getExternalStorageDirectory().getPath();
                        mediaType = MediaStore.Audio.Media.IS_MUSIC;
                        getSounds(filePath, mediaType);
                        break;
                }
                fillDatabase();
            }
        };
        btnDownloads.setOnClickListener(btnListener);
        btnNotifications.setOnClickListener(btnListener);
        btnAll.setOnClickListener(btnListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getSounds(String filePath, String mediaType) {
        ContentResolver soundResolver = requireActivity().getContentResolver();
        String selection = mediaType + " != 0 AND " + MediaStore.Audio.Media.DATA + " LIKE '" + filePath + "/%'";
        Uri soundUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor soundCursor = soundResolver.query(soundUri, null, selection, null, " ARTIST asc");

        Log.d(TAG, "getSounds: selection: " + selection);

        if (soundCursor != null && soundCursor.moveToFirst()) {
            int id = soundCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int name = soundCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int path = soundCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int duration = soundCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do {
                String soundTitle = soundCursor.getString(id);
                long soundId = soundCursor.getLong(name);
                String soundPath = soundCursor.getString(path);
                int soundDuration = soundCursor.getInt(duration);

                Sound sound = new Sound(String.valueOf(soundId), soundTitle, soundPath, soundDuration);
                soundList.add(sound);
                Log.d(TAG, "getSounds: sound: " + sound.toString());
            }
            while (soundCursor.moveToNext());
            soundCursor.close();

            Log.d(TAG, "getSounds: sound: " + soundList.size());
        }
        mCallBack.onAdd();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fillDatabase() {
        progressBar.setMax(soundList.size());

        new Thread(new Runnable() {
            LiveData<List<Sound>> sounds;
            AppDatabase database = AppDatabase.getInstance(getContext());

            @Override
            public void run() {
                database.clearAllTables();

                for (Sound s : soundList) {
                    progressBar.incrementProgressBy(1);
                    Sound sound = new Sound(s.id, s.name, s.path, s.duration);
                    database.soundDAO().insert(sound);
                    Log.d(TAG, "fillDatabase: " + sound.toString());
                }
                Log.d(TAG, "------------------------------------------------");

                sounds = database.soundDAO().getAll();
            }
        }).start();
    }
}