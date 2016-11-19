package zju.homework.pdfviewer.Java;

/**
 * Created by stardust on 2016/11/17.
 */

public class Account {

    private String id;

    private String currentGroupId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public Account(String id, String pwd) {
        this.id = id;
        this.password = pwd;
//        this.currentGroupId = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(String currentGroupId) {
        this.currentGroupId = currentGroupId;
    }
}
