/*
  Author: Ethan Darling
  Class: CS 3270
  SoundViewModel.java
 */

package com.example.soundboard.ui.sound;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SoundViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SoundViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}