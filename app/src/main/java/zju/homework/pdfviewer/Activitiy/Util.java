package zju.homework.pdfviewer.Activitiy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.pspdfkit.annotations.Annotation;
import com.pspdfkit.annotations.FreeTextAnnotation;
import com.pspdfkit.annotations.HighlightAnnotation;
import com.pspdfkit.annotations.InkAnnotation;
import com.pspdfkit.annotations.NoteAnnotation;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import zju.homework.pdfviewer.Java.AnnotationMixin;
import zju.homework.pdfviewer.Java.IgnoreUnknownMixin;
import zju.homework.pdfviewer.Java.SubclassAnnotationMixin;


/**
 * Created by stardust on 2016/11/13.
 */

public class Util {

    private static final String HOST = "http://222.205.46.130:3000";
    public static final String URL_ACCOUNT = HOST + "/accounts";
    public static final String URL_ANNOTATION = HOST + "/annotations";
    public static final String URL_GROUP = HOST + "/groups";

    public static final int REQUEST_OPEN_DOCUMENT = 1;
    public static final int REQUEST_ASK_FOR_PERMISSION = 2;
    public static final int REQUEST_LOGIN = 3;

    public static final String LOG_TAG = Util.class.getName();

    public static File cacheDir;

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.addMixIn(RectF.class, IgnoreUnknownMixin.class);
        mapper.addMixIn(Annotation.class, AnnotationMixin.class);

        mapper.addMixIn(InkAnnotation.class, SubclassAnnotationMixin.class);
        mapper.addMixIn(FreeTextAnnotation.class, SubclassAnnotationMixin.class);
        mapper.addMixIn(HighlightAnnotation.class, SubclassAnnotationMixin.class);
        mapper.addMixIn(NoteAnnotation.class, SubclassAnnotationMixin.class);
    }

    public static void setCacheDir(File dir){
        cacheDir = dir;
    }

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
        intent.setType("*/*");

        activity.startActivityForResult(intent, REQUEST_OPEN_DOCUMENT);

    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static String getStringFromInputStream(InputStream is) throws IOException{

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;

        while ( (line = br.readLine()) != null ){
            sb.append(line);
        }

        is.close();
        br.close();
        return sb.toString();
    }

    public static String inputStreamToBase64(InputStream is){
        byte[] bytes = null;
        try{
             bytes = getBytesFromInputStream(is);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return new String(Base64.encode(bytes, Base64.DEFAULT));

    }

    public static Uri base64ToFile(String base64str, File tmpFile){
        try{
            FileOutputStream fout = new FileOutputStream(tmpFile);
            fout.write(Base64.decode(base64str, Base64.DEFAULT));
            fout.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return Uri.fromFile(tmpFile);
    }

//    private static byte[] loadFile(File file) throws IOException {
//        InputStream is = new FileInputStream(file);
//
//        long length = file.length();
//        if (length > Integer.MAX_VALUE) {
//            // File is too large
//        }
//        byte[] bytes = new byte[(int)length];
//
//        int offset = 0;
//        int numRead = 0;
//        while (offset < bytes.length
//                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
//            offset += numRead;
//        }
//
//        if (offset < bytes.length) {
//            throw new IOException("Could not completely read file "+file.getName());
//        }
//
//        is.close();
//        return bytes;
//    }

    public static void userLogin(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);

        activity.startActivityForResult(intent, REQUEST_LOGIN);
    }


    public static String objectToJson(Object obj){
        try{
            return mapper.writeValueAsString(obj);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object jsonToObject(String json, Class cls){
        try{
            Object obj = mapper.readValue(json, cls);
//            Log.v(LOG_TAG, "content = " + json);
            return obj;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }


    public static Object jsonToObject(String json, TypeReference type){
        try{
            Object obj = mapper.readValue(json, type);
            Log.v(LOG_TAG, "content = " + json);
            return obj;
        }
        catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

//
//    public static String objectToJson(Object obj, String filePath){
//
//        try{
//            File file = File.createTempFile(filePath, "json", cacheDir);
//            mapper.writeValue(file, obj);
//            return file.getAbsolutePath();
//        }
//        catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//    }

//    public static Object jsonToObject(String absolutePath, Class cls){
//        try{
//            File file = new File(absolutePath);
//            Scanner in = new Scanner(new FileReader(file));
//            Object obj = mapper.readValue(file, cls);
//            Log.v(LOG_TAG, "filepath = " + absolutePath + ", content = " + in.toString());
//            return obj;
//        }
//
//        catch (IOException ex){
//            ex.printStackTrace();
//            return null;
//        }
//    }

}


