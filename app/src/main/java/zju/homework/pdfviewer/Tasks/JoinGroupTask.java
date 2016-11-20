package zju.homework.pdfviewer.Tasks;

import android.os.AsyncTask;

import java.security.InvalidParameterException;

import zju.homework.pdfviewer.Java.Group;
import zju.homework.pdfviewer.Utils.NetworkManager;
import zju.homework.pdfviewer.Utils.Util;

/**
 * Created by stardust on 2016/11/19.
 */


public class JoinGroupTask extends AsyncTask<Object, Void, Object> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }       // @MainThread

    /**
     *
     * @param params is [ GroupId ] .
     */
    @Override
    protected Object doInBackground(Object... params) {         // WorkerThread
        if( params.length < 1 )
            throw  new InvalidParameterException("Not enough params in create group task") ;

        String id = (String)params[0];

        NetworkManager networkManager = new NetworkManager();

        String result = networkManager.getJson(Util.URL_GROUP + "/" + id);
        if( result == null || result.length() == 0 ){
            return null;
        }
        Group group = (Group) Util.jsonToObject(result, Group.class);
        return group;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object res) {        // @MainThread
        super.onPostExecute(res);

    }
}