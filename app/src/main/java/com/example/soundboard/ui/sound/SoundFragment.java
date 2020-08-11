/*
  Author: Ethan Darling
  Class: CS 3270
  SoundFragment.java
 */

package com.example.soundboard.ui.sound;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundboard.R;
import com.example.soundboard.db.Sound;
import com.example.soundboard.ui.recycler.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that holds the recycler view and all of it's information.
 */
public class SoundFragment extends Fragment {

    // Fields
    private List<Sound> soundList = new ArrayList<>();

    private SoundViewModel soundViewModel;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    public OnSoundClickListener mCallBack;
    private static final String TAG = "MainActivity";

    View root;


    /**
     * Interface fore when a sounds is clicked on.
     */
    public interface OnSoundClickListener {
        void SoundClicked(int position, View v);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnSoundClickListener)
            mCallBack = (OnSoundClickListener) context;
        else
            throw new ClassCastException(context.toString() + "Must implement sound fragment listener");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        soundViewModel = ViewModelProviders.of(this).get(SoundViewModel.class);
        root = inflater.inflate(R.layout.fragment_sound, container, false);
        final TextView textView = root.findViewById(R.id.text_sound);
        soundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        initRecyclerView(root);

        adapter.setOnSoundClickListener(new RecyclerViewAdapter.ClickListener() {
            @Override
            public void onSoundClick(int position, View v) {
                mCallBack.SoundClicked(position, v);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();

        ViewModelProviders.of(this)
                .get(AllSoundViewModel.class)
                .getSoundList(context)
                .observe(this, new Observer<List<Sound>>() {
                    @Override
                    public void onChanged(List<Sound> sounds) {
                        if (sounds != null) {
                            adapter.addItems(sounds);
                        }
                    }
                });
    }

    /**
     * Initializes the recycler view and it's adapter.
     * @param view View
     */
    private void initRecyclerView(View view) {
        Log.d(TAG, "iniRecyclerView: init recyclerview.");
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(getContext(), soundList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}