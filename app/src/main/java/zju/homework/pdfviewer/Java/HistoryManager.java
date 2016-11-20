package zju.homework.pdfviewer.Java;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 马啸远 on 2016/11/18.
 */

public class HistoryManager {
    private List<History> mHistories;
    private ArrayList<String> mHistoryListData = new ArrayList<>();

    public HistoryManager() {
        mHistories = new ArrayList<>();
    }

    public void initHistories(Context context) {
        SharedPreferences pref = context.getSharedPreferences("history",
                MODE_PRIVATE);

        int i = 0;
        String formedHistory = pref.getString("history" + i, null);
        while (formedHistory != null) {
            String[] tmp = formedHistory.split("\\$");
            String time = tmp[0];
            String pdfName = "";
            int length = tmp.length;
            for(int j = 1; j < length; j++) {
                pdfName += tmp[j];
            }

            Log.d("test", time + " " + pdfName);

            addHistory(time, pdfName);

            i++;
            formedHistory = pref.getString("history" + i, null);
        }
    }

    public List<History> getHistories() {
        return mHistories;
    }

    public Uri getPdfUriByindex(int index) {
        History history = mHistories.get(mHistories.size() - index - 1);
        String pdfName = history.getPDFName();
        return Uri.parse(pdfName);
    }

    public void addHistory(String time, String pdfName) {
        //delete same pdf history
        int i = 0;
        for(History history : mHistories) {
            if(history.getPDFName().equals(pdfName)) {
                int size = mHistories.size();
                mHistories.remove(i);
                mHistoryListData.remove(size - i - 1);
                break;
            }
            i++;
        }

        History history = new History(time, pdfName);
        mHistories.add(history);
        mHistoryListData.add(0, history.getListviewHistory());
    }

    public void clearHistories() {
        mHistories.clear();
        mHistoryListData.clear();
    }

    public void saveHistories(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("history", MODE_PRIVATE).edit();
        editor.clear();

        int i = 0;
        for(History history : mHistories) {
            Log.d("test", history.getFormedHistory());

            editor.putString("history" + i, history.getFormedHistory());
            i++;
        }

        editor.commit();
    }

    public ArrayList<String> getListData() {
        return mHistoryListData;
    }
}
