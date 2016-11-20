package zju.homework.pdfviewer.Tasks;

import android.os.AsyncTask;

import java.security.InvalidParameterException;

import zju.homework.pdfviewer.Utils.NetworkManager;

/**
 * Created by stardust on 2016/11/20.
 */

public class DownloadDocumentTask extends AsyncTask<Object, Void, Boolean> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     *
     * @param params is [ Addr, fileAddr ] .
     */
    @Override
    protected Boolean doInBackground(Object... params){
        if( params.length < 2 )
            throw  new InvalidParameterException("Not enough params in downloadDocument") ;

        String addr = (String )params[0];
        String filepath = (String) params[1];

        NetworkManager networkManager = new NetworkManager();
//        String result;

        return networkManager.getDocument(addr, filepath);
    }

    @Override
    protected void onPostExecute(Boolean responseMsg){
        super.onPostExecute(responseMsg);
    }

}
