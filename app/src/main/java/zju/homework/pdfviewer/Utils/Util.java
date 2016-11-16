package zju.homework.pdfviewer.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import zju.homework.pdfviewer.Activitiy.MainActivity;


/**
 * Created by stardust on 2016/11/13.
 */

public class Util {

    public static final int REQUEST_OPEN_DOCUMENT = 1;
    public static final int REQUEST_ASK_FOR_PERMISSION = 2;

    public static boolean requestExternalStorageRwPermission(@NonNull Activity activity, int requestCode) {
        // On Android 6.0+ we ask for SD card access permission.
        // Since documents can be annotated we ask for write permission as well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        requestCode);
                return false;
            }
        }
        return true;
    }

    public static void showOpenFileDialog(@NonNull Activity activity){
        Intent intent = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/*");

        activity.startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);

    }

    public static String getStringFromInputStream(InputStream is, int MAX_BUFFER) throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[MAX_BUFFER];
        int len = -1;

        while ( (len = is.read(buffer) ) != -1){
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();
        os.close();
        return state;
    }



}


