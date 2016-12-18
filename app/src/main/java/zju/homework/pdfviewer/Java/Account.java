package zju.homework.pdfviewer.Java;

import android.database.CursorIndexOutOfBoundsException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.security.SecureRandom;

/**
 * Created by stardust on 2016/11/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Account {
    private String id;
    private Group currentGroupId = null;

    public Account() {}

    public Account(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public Group getGroup() {
        return currentGroupId;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        else if(o.getClass() == this.getClass()) {
            Account tmp = (Account)o;
            if(id.equals(tmp.getID())) {
                return true;
            }
        }
        return false;
    }


    //创建组
    public boolean hasGroup() {
        if(currentGroupId == null) {
            return false;
        }
        return true;
    }

    public boolean setGroup(Group group) {
        currentGroupId = group;
        return true;
    }

    public boolean quitGroup() {
        if(currentGroupId == null) {
            return false;
        }
        else {
            //.............退出的操作
            currentGroupId = null;
            return true;
        }
    }
}


//
//public class Account {
//
//    private String id;
//
//    private String currentGroupId;
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    private String password;
//
//    public Account(String id, String pwd) {
//        this.id = id;
//        this.password = pwd;
////        this.currentGroupId = group;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getCurrentGroupId() {
//        return currentGroupId;
//    }
//
//    public void setCurrentGroupId(String currentGroupId) {
//        this.currentGroupId = currentGroupId;
//    }
//}
