package zju.homework.pdfviewer.Tasks;

import android.os.AsyncTask;

import java.security.InvalidParameterException;

import zju.homework.pdfviewer.Java.Group;
import zju.homework.pdfviewer.Java.ResponseMsg;
import zju.homework.pdfviewer.Utils.NetworkManager;
import zju.homework.pdfviewer.Activitiy.Util;

/**
 * Created by stardust on 2016/11/18.
 */


public class CreateGroupTask extends AsyncTask<Object, Void, Boolean> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }       // @MainThread

    /**
     *
     * @param params is [ Group ] .
     */
    @Override
    protected Boolean doInBackground(Object... params) {         // WorkerThread
        if( params.length < 1 )
            throw  new InvalidParameterException("Not enough params in create group task") ;

        Group group = (Group)params[0];

        NetworkManager networkManager = new NetworkManager();
        String result = networkManager.postJson(Util.URL_GROUP,
                Util.objectToJson(
                        group
        ));
        if( result == null ){
            return false;
        }
        ResponseMsg responseMsg = (ResponseMsg) Util.jsonToObject(result, ResponseMsg.class);
        if( responseMsg != null && responseMsg.getMessage().equals("Create Group Success") ){
            return true;
        }else{
            return false;
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean res) {        // @MainThread
        super.onPostExecute(res);

    }
}