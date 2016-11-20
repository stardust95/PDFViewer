package zju.homework.pdfviewer.Java;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by stardust on 2016/11/20.
 */

public class History {
    private String mTime;
    private String mPDFName;

    public History() {}

    public History(String time, String pdfName) {
        mTime = time;
        mPDFName = pdfName;
    }

    public String getTime() {
        return mTime;
    }

    public String getPDFName() {
        return mPDFName;
    }

    public String getFormedHistory() {
        return mTime + "$" + mPDFName;
    }

    public String getListviewHistory() {
        String tmp = null;
        try {
            tmp = URLDecoder.decode(mPDFName, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String[] tt = tmp.split("\\/");

        return tt[tt.length - 1] + "\n" + mTime;
    }

    public boolean equals(History history) {
        return (mTime.equals(history.getTime()) && mPDFName.equals(history.getPDFName()));
    }
}