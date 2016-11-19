package zju.homework.pdfviewer.Java;

import android.app.job.JobInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by stardust on 2016/11/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnnotationData {
    private String groupId;

    private String account;

    private String jsonData;

    public AnnotationData(){

    }

    public AnnotationData(String g, String ac, String json){
        groupId = g;
        account = ac;
        jsonData = json;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }
}
