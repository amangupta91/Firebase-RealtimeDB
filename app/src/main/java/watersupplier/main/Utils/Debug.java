package watersupplier.main.Utils;

import android.util.Log;

/**
 * Created by aman on 23/4/18.
 */

public class Debug {
    //set isDebug false while make final build
    public static final boolean isDebug = true;

    public static void PrintLogError(String TAG, String MSG) {
        if (isDebug) {
            Log.e(TAG, "PrintLogError: " + MSG);
        }
    }
}
