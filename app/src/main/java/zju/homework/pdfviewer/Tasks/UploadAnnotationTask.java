package zju.homework.pdfviewer.Tasks;

import android.os.AsyncTask;
import android.view.animation.AnticipateOvershootInterpolator;

import com.pspdfkit.annotations.Annotation;

import java.security.InvalidParameterException;

import zju.homework.pdfviewer.Java.Account;
import zju.homework.pdfviewer.Java.AnnotationData;
import zju.homework.pdfviewer.Utils.NetworkManager;
import zju.homework.pdfviewer.Activitiy.Util;

/**
 * Created by stardust on 2016/11/18.
 */

public class UploadAnnotationTask extends AsyncTask<Object, Void, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    /**
     *
     * @param params is [ AnnotationData] .
     */
    @Override
    protected String doInBackground(Object... params) {
        if( params.length < 1 )
            throw  new InvalidParameterException("Not enough params in upload annotation") ;

        AnnotationData annotationData = (AnnotationData)params[0];
//        annotationData.setJsonData(Util.objectToJson(annotation));
//        annotationData.setAccount(Util.objectToJson(account.getId()));
//        annotationData.setGroupId(account.getCurrentGroupId());

        NetworkManager networkManager = new NetworkManager();
        String result = networkManager.postJson(Util.URL_ANNOTATION, Util.objectToJson(annotationData));
//        ResponseMsg<AnnotationData> result = networkManager.postJsonAnnotation(Util.URL_ANNOTATION, Util.objectToJson(annotation));
//        if( result != null )
//            return result.getMessage();
        return result;
    }
}
