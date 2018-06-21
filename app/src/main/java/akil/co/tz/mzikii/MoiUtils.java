package akil.co.tz.mzikii;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Waky on 11/25/2017.
 */

public class MoiUtils {
    public static String formatSessionTime(long time){
        try {
            Date date = new Date(time);
            SimpleDateFormat diffFormat = new SimpleDateFormat(
                    "HH:mm", Locale.getDefault());
            return diffFormat.format(date);
        } catch (Exception e) {
            Log.e("WOURA", "FormatSessionTime - Parsing datetime failed", e);
            return "" + time;
        }
    }

    public static String[] daysOfWeek(boolean short_names){
        if(short_names)
            return new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        return new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    }

    public static String dayNameFromId(int id, boolean short_name){
        return daysOfWeek(short_name)[id];
    }

    public static void copyFile(FileInputStream sourceFile, File destFile) throws IOException {
        Exception ex = null;

        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = sourceFile.getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e){
            Log.i("WOURA", "Couldn't do the copy!");
            ex = e;
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }

            if(ex == null)
                Log.i("WOURA", "Successfully copied file!");
        }
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()){
            vId = matcher.group(1);
        }else{
            if(ytUrl.contains("watch?v=")){
                vId = ytUrl.substring(ytUrl.indexOf("watch?v=") + 8, ytUrl.length());
            }
        }
        return vId;
    }
}
