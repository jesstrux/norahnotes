package akil.co.tz.mzikii;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.Date;

public class ReceiveIntentActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_intent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            } else if (type.startsWith("audio/")) {
                Permissions.check(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        "Storage permissions are required because...", new Permissions.Options()
                                .setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Info"),
                        new PermissionHandler() {
                            @Override
                            public void onGranted() {
                                handleSendAudio(intent);
                            }
                        });
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Log.d("WOURA", "Shared text:" + sharedText);
        }
    }

    void handleSendAudio(Intent intent) {
        String mBasePath = Environment.getExternalStorageDirectory().getPath();
        File appFolder = new File(mBasePath, Constants.APP_FOLDER);
        if(!appFolder.exists()){
            Log.d("WOURA", "App folder doesn't exist, creating one....");
            if(appFolder.mkdirs()){
                Log.d("WOURA", "App folder was created....");
            }
        }

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Date d = new Date();

        try {
            File dummy_file = new File(appFolder, "filename.asgdsag");
            if (!dummy_file.exists()){
                dummy_file.createNewFile();
            }

            InputStream is = new BufferedInputStream(new FileInputStream(dummy_file));
            String mimeType = URLConnection.guessContentTypeFromStream(is);

            File dest_file = new File(appFolder, "mziki_" + d.getTime() + "_" + getFileName(imageUri));

            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            MoiUtils.copyFile((FileInputStream) inputStream, dest_file);

            Log.d("WOURA", "File was successfully copied!!");
            Log.d("WOURA", "File path is: " + dest_file.getAbsolutePath());

            playFile(dest_file.getAbsolutePath());
        } catch (Exception e) {
            Log.d("WOURA", "Couldn't copy file!!");
            Log.d("WOURA", "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playFile(String destination){
        try {
            mediaPlayer.setDataSource(destination);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch(Exception e){
            Log.d("WOURA", "Play file Error: " + e.getMessage());
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }
}
