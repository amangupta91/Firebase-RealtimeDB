package watersupplier.main.Firsebase_Operations;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by PALASH on 30/12/16.
 */
public class FirebaseInstance {

    private static boolean persistenceEnable = false;
    private static FirebaseDatabase Database;

    public static FirebaseDatabase getInstance() {
        if (Database == null) {
            Database = FirebaseDatabase.getInstance();
        }
        return Database;
    }
}
