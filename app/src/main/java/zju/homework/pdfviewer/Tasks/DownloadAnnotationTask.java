package zju.homework.pdfviewer.Tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.pspdfkit.annotations.Annotation;

import java.security.InvalidParameterException;
import java.util.List;

import zju.homework.pdfviewer.Java.Account;
import zju.homework.pdfviewer.Java.AnnotationData;
import zju.homework.pdfviewer.Java.ResponseMsg;
import zju.homework.pdfviewer.Utils.NetworkManager;
import zju.homework.pdfviewer.Activitiy.Util;

/**
 * Created by stardust on 2016/11/18.
 */

public class DownloadAnnotationTask extends AsyncTask<Object, Void, String> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     *
     * @param params is [ Account, GroupId ] .
     */
    @Override
    protected String doInBackground(Object... params){
        if( params.length < 2 )
            throw  new InvalidParameterException("Not enough params in sync annotation") ;

        String account = (String )params[0];
        String groupid = (String) params[1];

        AnnotationData annotationData = new AnnotationData();
        annotationData.setGroupId(groupid);
        annotationData.setAccount(account);

        NetworkManager networkManager = new NetworkManager();
//        String result;
        String result = networkManager.postJson(Util.URL_ANNOTATION + "/sync", Util.objectToJson(annotationData));
        return result;
    }

    @Override
    protected void onPostExecute(String responseMsg){

    }

}
