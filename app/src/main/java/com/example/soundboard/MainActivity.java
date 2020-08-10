package com.example.soundboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.soundboard.ui.dashboard.AddSoundFragment;
import com.example.soundboard.ui.recycler.RecyclerViewAdapter;
import com.example.soundboard.ui.sound.HelpDialog;
import com.example.soundboard.ui.sound.SoundDetailsDialog;
import com.example.soundboard.ui.sound.SoundFragment;
import com.example.soundboard.ui.sound.SoundSearchDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AddSoundFragment.OnSoundFragmentListener,
        SoundFragment.OnSoundClickListener,
        SoundSearchDialog.OnSoundFragmentListener {

    // Fields
    private static final int MY_PERMISSION_REQUEST = 1;

    private FragmentManager fm;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: started.");

        // Check for storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        // Initialize bottom navigation and app bar.
        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_sound, R.id.navigation_addSound)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                searchSounds();
                return true;
            case R.id.help:
                help();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Starts a sound search event.
     */
    private void searchSounds() {
        SoundSearchDialog searchDialog = new SoundSearchDialog();
        searchDialog.setCancelable(true);
        searchDialog.show(getSupportFragmentManager(), "searchDialog");
    }


    /**
     * Shows the help dialog.
     */
    private void help() {
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setCancelable(true);
        helpDialog.show(getSupportFragmentManager(), "helpDialog");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    public void onAdd(final int soundCount) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (soundCount > 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            String.format(Locale.US, "%d %s", soundCount, "sounds found"),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 156);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            String.format(Locale.US, "%s", " No sounds found"),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 156);
                    toast.show();
                }
            }
        });
        RecyclerViewAdapter.resetPlayer();
    }

    @Override
    public void SoundClicked(int position, View v) {
        SoundDetailsDialog soundDetailsDialog = new SoundDetailsDialog();
        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(android.R.id.content, soundDetailsDialog)
                .addToBackStack(null)
                .commit();

        soundDetailsDialog.showSoundDetails(position, v);
    }
}