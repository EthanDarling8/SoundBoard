package com.example.soundboard.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SoundDAO {

    @Query("select * from Sound")
    LiveData<List<Sound>> getAll();

    @Query("select count(id) from Sound")
    int getSoundCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Sound sound);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Sound sound);

    @Delete
    void delete(Sound sound);

    @Query("select * from Sound where id = :id")
    List<Sound> loadByID(int id);

    @Query("select * from Sound where name like :search")
    List<Sound> searchByName(String search);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertSounds(ArrayList<Sound> soundList);
}
