package zju.homework.pdfviewer.Tasks;

import android.os.AsyncTask;

import java.security.InvalidParameterException;

import zju.homework.pdfviewer.Java.Group;
import zju.homework.pdfviewer.Java.ResponseMsg;
import zju.homework.pdfviewer.Utils.NetworkManager;
import zju.homework.pdfviewer.Utils.Util;

/**
 * Created by stardust on 2016/11/20.
 */


public class QuitGroupTask extends AsyncTask<Object, Void, Object> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }       // @MainThread

    /**
     *
     * @param params is [ Group ] .
     */
    @Override
    protected Group doInBackground(Object... params) {         // WorkerThread
        if( params.length < 1 )
            throw  new InvalidParameterException("Not enough params in create group task") ;

        Group group = (Group)params[0];

        NetworkManager networkManager = new NetworkManager();
        String result = networkManager.postJson(Util.URL_GROUP + "/quit",
                Util.objectToJson(
                        group
                ));
        if( result == null ){
            return null;
        }
        ResponseMsg responseMsg = (ResponseMsg) Util.jsonToObject(result, ResponseMsg.class);

        if( responseMsg != null && responseMsg.getMessage().equals("Quit Group Success") ){
            return group;
        }else{
            return null;
        }

    }

    @Override
    protected void onPostExecute(Object res) {        // @MainThread
        super.onPostExecute(res);

    }
}