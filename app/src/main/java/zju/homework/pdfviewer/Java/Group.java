package zju.homework.pdfviewer.Java;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import java.io.IOException;
import java.util.HashSet;

/**
 * define the object of a working group
 * @author MaXiaoyuan
 * @version 1.0
 * @date 2016/11/15
 */


@JsonDeserialize(using = GroupDeserializer.class)
@JsonSerialize(using = GroupSerializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {

    public static final int GROUPID_MIN = 10000;

    public static final int GROUPID_MAX = 99999;

    private final String id;

    private final Account creator;

    private String pdfData;

    private String fileName = null;

    public Group(String id, Account creator, String pdfData, String fileName) {
        this.id = id;
        this.creator = creator;
        this.pdfData = pdfData;
        this.fileName = fileName;
        mAccounts.add(this.creator);
    }

    public Account getCreator() {
        return creator;
    }

    @JsonIgnore
    private HashSet<Account> mAccounts = new HashSet<>();

    public String getId() {
        return id;
    }

    public String getPdfData() {
        return pdfData;
    }

    public void setPdfData(String pdfData) {
        this.pdfData = pdfData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public HashSet<Account> getmAccounts() {
        return mAccounts;
    }

    public void setmAccounts(HashSet<Account> mAccounts) {
        this.mAccounts = mAccounts;
    }

    //组内添加用户
    public boolean addUser(Account account) {
        if(mAccounts.contains(account)) {
            return false;
        }
        mAccounts.add(account);
        return true;
    }

    //用户退出组
    public void removeUser(Account account) {
        mAccounts.remove(account);
    }
}

//
///**
// * Created by stardust on 2016/11/17.
// */
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class Group {
//
//    private String id;
//
//    private String pdfData;
//
//    private String fileName;
//
//    private String creator;
//
//    public String getCreator() {
//        return creator;
//    }
//
//    public void setCreator(String creator) {
//        this.creator = creator;
//    }
//
//    public Group(){
//
//    }
//
//    public Group(String id, String data, String name, String c){
//        this.id = id;
//        pdfData = data;
//        fileName = name;
//        creator = c;
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
//    public String getPdfData() {
//        return pdfData;
//    }
//
//    public void setPdfData(String pdfData) {
//        this.pdfData = pdfData;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
//}
