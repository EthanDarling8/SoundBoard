package com.example.soundboard.ui.dashboard;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.example.soundboard.ui.sound.SoundFinder;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a fragment for adding sounds from a particular folder.
 */
public class AddSoundFragment extends Fragment {

    // Fields
    private List<Sound> soundList;
    private String filePath, mediaType;
    private int soundCount = 0;

    private ProgressBar progressBar;
    private SoundFinder soundFinder = new SoundFinder();

    private OnSoundFragmentListener mCallBack;
    private static final String TAG = "AddSoundFragment";

    View root;

    /**
     * Interface used to add sounds to the room database
     */
    public interface OnSoundFragmentListener {
        void onAdd(int soundCount);
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
        AddSoundViewModel addSoundViewModel = ViewModelProviders.of(this).get(AddSoundViewModel.class);
        root = inflater.inflate(R.layout.fragment_add_sound, container, false);
        final TextView textView = root.findViewById(R.id.text_addSound);
        addSoundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

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
                        soundList = soundFinder.getSounds(requireActivity(), filePath, mediaType);
                        break;
                    case R.id.button_notifications:
                        filePath = Environment.getExternalStorageDirectory().getPath();
                        mediaType = MediaStore.Audio.Media.IS_NOTIFICATION;
                        soundList = soundFinder.getSounds(requireActivity(), filePath, mediaType);
                        break;
                    case R.id.button_all:
                        filePath = Environment.getExternalStorageDirectory().getPath();
                        mediaType = MediaStore.Audio.Media.IS_MUSIC;
                        soundList = soundFinder.getSounds(requireActivity(), filePath, mediaType);
                        break;
                }
                fillDatabase();
            }
        };

        btnDownloads.setOnClickListener(btnListener);
        btnNotifications.setOnClickListener(btnListener);
        btnAll.setOnClickListener(btnListener);

    }

    /**
     * Fills database with sounds from the selected location.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fillDatabase() {
        soundCount = 0;
        progressBar.setMax(soundList.size());

        new Thread(new Runnable() {
            LiveData<List<Sound>> sounds;
            AppDatabase database = AppDatabase.getInstance(getContext());

            @Override
            public void run() {
                database.clearAllTables();

                for (Sound s : soundList) {
                    progressBar.incrementProgressBy(1);
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