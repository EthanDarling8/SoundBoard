package com.example.soundboard.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.soundboard.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddSoundFragment extends Fragment {

    private AddSoundViewModel addSoundViewModel;
    private static final String TAG = "AddSoundFragment";
    private int soundEditID = 0;
    private int REQ_CODE_PICK_SOUNDFILE = 0;
    private Uri audio;

    private TextInputEditText sId, sName;
    private String sPath;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addSoundViewModel = ViewModelProviders.of(this).get(AddSoundViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_sound, container, false);
        final TextView textView = root.findViewById(R.id.text_addSound);
        addSoundViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        insertSound();

        sId = root.findViewById(R.id.text_sound_id);
        sName = root.findViewById(R.id.text_sound_name);
        sPath = "Test";

        return root;
    }

    private void insertSound() {
        getAudioFile();

        saveAudioFile();

//        new Thread(new Runnable() {
//            LiveData<List<Sound>> soundList;
//            AppDatabase database = AppDatabase.getInstance(getContext());
//            @Override
//            public void run() {
//                Sound sound = new Sound();
//
//                soundList = database.soundDAO().getAll();
//
//                List<Sound> temp = database.soundDAO().loadByID(soundEditID);
//                if (temp.isEmpty()) {
//                    database.soundDAO().insert(sound);
//                } else {
//                    database.soundDAO().delete(temp.get(0));
//                    database.soundDAO().insert(sound);
//                }
//
//                //database.clearAllTables();
//                Log.d(TAG, "insertSound: " + sound.toString());
//                Log.d(TAG, "------------------------------------------------");
//            }
//        }).start();
    }

    private void getAudioFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        assert data != null;
        audio = data.getData();

        Log.d(TAG, "onActivityResult: sound file created: " + audio.getPath());
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveAudioFile() {
//        try {
//            AssetFileDescriptor afd = getContext().getAssets().openFd(audio.getPath());
//            afd.createOutputStream().write();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}