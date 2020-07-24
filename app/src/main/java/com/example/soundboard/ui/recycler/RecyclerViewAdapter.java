package com.example.soundboard.ui.recycler;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundboard.R;
import com.example.soundboard.db.Sound;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private List<Sound> soundList;
    private Context context;
    private static MediaPlayer player = new MediaPlayer();
    private static MediaPlayer player2 = new MediaPlayer();
    private static ClickListener clickListener;
    private boolean looping = false;

    public RecyclerViewAdapter(Context context, List<Sound> soundList) {
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

        int duration = sound.getDuration();
        int mns = (duration / 60000) % 60000;
        int scs = (duration % 60000) / 1000;

        holder.name.setText(sound.getName());
        holder.duration.setText(String.format(Locale.US, "%01d:%02d", mns, scs));
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fileString = soundList.get(position).getPath();
                Uri uri = Uri.parse(fileString);

                player = new MediaPlayer();


                if (!looping) {
                    try {
                        player.reset();
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
                } else {
                    player.reset();
                }

                Log.d(TAG, "onClick: clicked on: " + soundList.get(position).getPath());

            }
        });

        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                }
            }
        });

        holder.loop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    looping = true;
                    player2 = new MediaPlayer();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String fileString = soundList.get(position).getPath();
                            Uri uri = Uri.parse(fileString);

                            try {
                                player2.reset();
                                player2.setLooping(true);
                                player2.setDataSource(context.getApplicationContext(), uri);
                                player2.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            player2.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    player2.start();
                                }
                            });
                        }
                    }).start();
                } else {
                    looping = false;
                    player2.reset();
                }
            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onSoundClick(Integer.parseInt(sound.getId()), holder.parentLayout);
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
        TextView name, duration;
        CardView parentLayout;
        Button play, pause;
        ToggleButton loop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_sound_name);
            duration = itemView.findViewById(R.id.item_sound_duration);
            play = itemView.findViewById(R.id.item_play_button);
            pause = itemView.findViewById(R.id.item_pause_button);
            loop = itemView.findViewById(R.id.item_loop_button);
            parentLayout = itemView.findViewById(R.id.item_parent_layout);

        }
    }

    public static void resetPlayer() {
        player.stop();
    }

    public void setOnSoundClickListener(ClickListener clickListener) {
        RecyclerViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onSoundClick(int id, View v);
    }
}
