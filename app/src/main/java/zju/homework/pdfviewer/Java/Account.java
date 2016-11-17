package zju.homework.pdfviewer.Java;

/**
 * Created by stardust on 2016/11/17.
 */

public class Account {

    private String id;

    private String currentGroupId;

    public Account(String id, String currentGroupId) {
        this.id = id;
        this.currentGroupId = currentGroupId;
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
