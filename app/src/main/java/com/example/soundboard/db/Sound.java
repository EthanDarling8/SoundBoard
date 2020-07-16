package com.example.soundboard.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Sound {

    @PrimaryKey(autoGenerate = true)
    public int _id;

    public String id;
    public String name;
    public String path;
    public int duration;

    public Sound(String id, String name, String path, int duration) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.duration = duration;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @NotNull
    @Override
    public String toString() {
        return "Sound{" +
                "pid=" + _id +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
