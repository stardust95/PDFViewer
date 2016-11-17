package zju.homework.pdfviewer.Utils;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.net.HttpURLConnection;
import java.net.URL;

import zju.homework.pdfviewer.Activitiy.PDFViewActivity;

/**
 * Created by stardust on 2016/11/16.
 */

public class NetworkManager {

    public final String HOST = "http://localhost:60987";
    public final String URL_ACCOUNT = HOST + "/tables/account";
    public final String URL_ANNOTATION = HOST + "/tables/annotation";
    public final String URL_GROUP = HOST + "/tables/group";

    final static String LOG_TAG = "***NETWORK***";

    final static int RESPONSE_OK = 200;

    final static int MAX_BUFFER = 1024;

    public NetworkManager(){ }

    public String getJson(String addr){
        String result = null;
        try {
            URL url = new URL(addr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "");
            connection.setDoInput(true);
            connection.connect();

            int response = connection.getResponseCode();
            if( response == RESPONSE_OK ){
                InputStream is = connection.getInputStream();
                try{
                    result = Util.getStringFromInputStream(is);
                }catch (IOException ex){
                    ex.printStackTrace();
                }finally {
                    is.close();
                }

            }

        }catch (IOException ex){
            ex.printStackTrace();
        }

        return result;
    }

    public String postJson(String addr, String json){
        String result = null;
        try{
            URL url = new URL(addr);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();

            int response = connection.getResponseCode();
            Log.i(LOG_TAG, connection.getResponseMessage());
            if( response == RESPONSE_OK ){
                InputStream is = connection.getInputStream();
                try {
                     result = Util.getStringFromInputStream(is);
                }
                catch (IOException ex){
                    ex.printStackTrace();
                }
                finally {
                    is.close();
                }
            }else {
                Log.i(LOG_TAG, "Post Failed, Response Message:" + connection.getResponseMessage());
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }
        return result;
    }



}
