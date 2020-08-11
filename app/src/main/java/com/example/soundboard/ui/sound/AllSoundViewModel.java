/*
  Author: Ethan Darling
  Class: CS 3270
  AllSoundViewModel.java
 */

package com.example.soundboard.ui.sound;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.soundboard.db.AppDatabase;
import com.example.soundboard.db.Sound;

import java.util.List;

public class AllSoundViewModel extends ViewModel {
    private LiveData<List<Sound>> soundList;

    public LiveData<List<Sound>> getSoundList(Context c) {
        if (soundList != null) {
            return soundList;
        }
        return soundList = AppDatabase.getInstance(c).soundDAO().getAll();
    }
}
