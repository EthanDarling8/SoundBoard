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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundboard.R;
import com.example.soundboard.db.Sound;
import com.example.soundboard.ui.recycler.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SoundFragment extends Fragment {

    private SoundViewModel soundViewModel;
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private int columnCount = 1;
    private List<Sound> soundList = new ArrayList<>();
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        soundList.add(new Sound("10", "Test", "/Test/Path"));

        soundViewModel = ViewModelProviders.of(this).get(SoundViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sound, container, false);
        final TextView textView = root.findViewById(R.id.text_sound);
        soundViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        initRecyclerView(root);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = getContext();

        ViewModelProviders.of(this)
                .get(AllSoundViewModel.class)
                .getCourseList(context)
                .observe(this, new Observer<List<Sound>>() {
                    @Override
                    public void onChanged(List<Sound> courses) {
                        if (courses != null) {
                            adapter.addItems(courses);
                        }
                    }
                });
    }

    private void initRecyclerView(View view) {
        Log.d(TAG, "iniRecyclerView: init recyclerview.");
        recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new RecyclerViewAdapter(getContext(), soundList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}