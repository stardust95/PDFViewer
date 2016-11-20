package zju.homework.pdfviewer.Utils;


import android.accounts.NetworkErrorException;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import zju.homework.pdfviewer.Activitiy.Util;

/**
 * Created by stardust on 2016/11/16.
 */

public class NetworkManager {

    final static String LOG_TAG = NetworkManager.class.getName();

    final static int RESPONSE_OK = 200;

    final static int MAX_BUFFER = 1024;

    public NetworkManager(){ }

    public boolean getDocument(String addr, String filepath){
        try {
            URL url = new URL(addr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "");
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if( responseCode == RESPONSE_OK ){
                InputStream is = connection.getInputStream();
                OutputStream os = new FileOutputStream(filepath);
                try{
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    while ( (read = is.read(bytes)) != -1 ){
                        os.write(bytes, 0, read);
                    }
                }catch (IOException ex){
                    ex.printStackTrace();
                    return false;
                }finally {
                    try {
                        is.close();
                        os.close();
                    }catch (IOException ex){
                        ex.printStackTrace();
                        return false;
                    }
                }

            }

        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public String getJson(String addr){

        String result = null;
        try {
            URL url = new URL(addr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "");
            connection.setDoInput(true);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if( responseCode == RESPONSE_OK ){
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
//            connection.setRequestProperty("User-Agent", "");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes());
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            Log.i(LOG_TAG, connection.getResponseMessage());
            if( responseCode == RESPONSE_OK ){
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
                throw new NetworkErrorException( "Post Failed, Response Message:" + connection.getResponseMessage());
//                Log.i(LOG_TAG,);
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }catch (NetworkErrorException ex){
            ex.printStackTrace();
        }

        return result;
    }



}
