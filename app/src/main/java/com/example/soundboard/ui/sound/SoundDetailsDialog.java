package com.example.soundboard.ui.sound;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.soundboard.R;
import com.example.soundboard.db.AppDatabase;
import com.example.soundboard.db.Sound;

import java.util.List;
import java.util.Locale;


/**
 * Dialog Fragment that displays all of the details of a particular sound.
 */
public class SoundDetailsDialog extends DialogFragment {

    // Fields
    private List<Sound> sound;
    private TextView id, name, path, album, artist, duration, size;

    View root;

    public SoundDetailsDialog() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_sound_details_dialog, container, false);

        id = root.findViewById(R.id.sound_id);
        name = root.findViewById(R.id.sound_name);
        path = root.findViewById(R.id.sound_path);
        album = root.findViewById(R.id.sound_album);
        artist = root.findViewById(R.id.sound_artist);
        duration = root.findViewById(R.id.sound_duration);
        size = root.findViewById(R.id.sound_size);

        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }

    /**
     * Accesses the room database for a given sound by position and formats it's details in a list.
     * @param position int
     * @param v View
     */
    public void showSoundDetails(final int position, View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase database = AppDatabase.getInstance(getContext());
                sound = database.soundDAO().loadByID(position);

                int dur = sound.get(0).getDuration();
                final int mns = (dur / 60000) % 60000;
                final int scs = (dur % 60000) / 1000;

                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("showSoundDetails", sound.toString());
                        id.setText(String.format(Locale.US, "%s %s", id.getHint(), sound.get(0).getId()));
                        name.setText(String.format(Locale.US, "%s %s", name.getHint(), sound.get(0).getName()));
                        path.setText(String.format(Locale.US, "%s %s", path.getHint(), sound.get(0).getPath()));
                        album.setText(String.format(Locale.US, "%s %s", album.getHint(), sound.get(0).getAlbum()));
                        artist.setText(String.format(Locale.US, "%s %s", artist.getHint(), sound.get(0).getArtist()));
                        duration.setText(String.format(Locale.US, "%s %02d:%02d", duration.getHint(), mns, scs));
                        size.setText(String.format(Locale.US, "%s %s %s", size.getHint(), sound.get(0).getSize(), "bits"));
                    }
                });
            }
        }).start();
    }
}