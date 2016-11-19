package zju.homework.pdfviewer.Utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * To exit all the activities at one time
 * @author MaXiaoyuan
 * @version 1.0
 * @date 2016/11/15
 */

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for( Activity activity : activities ) {
            if(!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
