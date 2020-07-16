package com.example.soundboard.ui.recycler;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundboard.R;
import com.example.soundboard.db.Sound;

import java.io.IOException;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private List<Sound> soundList;
    private Context context;
    private static MediaPlayer player = new MediaPlayer();

    public RecyclerViewAdapter(Context context, List<Sound> soundList) {
        this.context = context;
        this.soundList = soundList;
    }

    public static void pausePlayer() {
        player.stop();
        player.reset();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_list_item, parent, false);
        player.stop();
        player.reset();
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
                player.stop();
                player.reset();

                Log.d(TAG, "onClick: clicked on: " + soundList.get(position).getPath());

                String fileString = soundList.get(position).getPath();
                Uri uri = Uri.parse(fileString);

                try {
                    player.setDataSource(context.getApplicationContext(), uri);
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        player.start();
                    }
                });

                //Toast.makeText(context, soundList.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addItems(List<Sound> sounds) {
        this.soundList.clear();
        this.soundList.addAll(sounds);
        notifyDataSetChanged();
        Log.d("RecyclerViewAdapter", "new Items Added: " + sounds.toString() + "\n");

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
