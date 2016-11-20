package zju.homework.pdfviewer.Java;

/**
 * Created by stardust on 2016/11/20.
 */

public class User{
    private String id;
    private String password;

    public User(){

    }

    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}