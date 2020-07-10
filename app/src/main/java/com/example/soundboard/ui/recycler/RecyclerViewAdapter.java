package com.example.soundboard.ui.recycler;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundboard.MainActivity;
import com.example.soundboard.R;
import com.example.soundboard.db.Sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";
    private List<Sound> soundList;
    private Context context;

    public RecyclerViewAdapter( Context context, List<Sound> soundList) {
        this.context = context;
        this.soundList = soundList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        final Sound sound = soundList.get(position);

        holder.id.setText(sound.getId());
        holder.name.setText(sound.getName());
        holder.path.setText(sound.getPath());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on: " + soundList.get(position).getPath());


                String fileString = soundList.get(0).getPath();
                File soundFile = new File(fileString);
                Uri uri = Uri.fromFile(soundFile);

//                try {
//                    ParcelFileDescriptor parcel = context.getContentResolver().openFileDescriptor(uri, "r", null);
//                    fInput = new FileInputStream(parcel.getFileDescriptor());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                MediaPlayer player = new MediaPlayer();
                try {
                    player.setDataSource(uri.getPath());
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(context, soundList.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addItems(List<Sound> sounds) {
        this.soundList.clear();
        this.soundList.addAll(sounds);
        notifyDataSetChanged();
        Log.d("test", "new Items Added: " + sounds.toString());

    }

    @Override
    public int getItemCount() {
        return soundList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, path;
        CardView parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.item_sound_id);
            name = itemView.findViewById(R.id.item_sound_name);
            path = itemView.findViewById(R.id.item_sound_path);
            parentLayout = itemView.findViewById(R.id.item_parent_layout);

        }
    }


}
