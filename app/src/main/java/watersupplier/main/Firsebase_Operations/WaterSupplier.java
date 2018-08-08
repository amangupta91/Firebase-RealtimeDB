package watersupplier.main.Firsebase_Operations;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Aman Gupta on 6/2/17.
 *
 * With This Class Offline Fire Base is implemented,as device connects ti internet data is SYNC to FireBase.
 * Data Limit = 10 mb ( used least recently)
 * Location = FireBase Cache
 */



/*
Enabling Offline Capabilities on Android


        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
        scoresRef.orderByValue().limitToLast(4).addChildEventListener(new ChildEventListener() {

@Override
public void onChildAdded(DataSnapshot snapshot, String previousChild) {
        System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
        }
        });

        Assume that the user loses connection, goes offline, and restarts the app. While still offline, the app queries for the last two items from the same location. This query will successfully return the last two items because the app had loaded all four items in the query above.

        scoresRef.orderByValue().limitToLast(2).addChildEventListener(new ChildEventListener() {
@Override
public void onChildAdded(DataSnapshot snapshot, String previousChild) {
        System.out.println("The " + snapshot.getKey() + " dinosaur's score is " + snapshot.getValue());
        }
        });

        In the preceeding example, the Firebase Realtime Database client raises 'child added' events for the highest scoring two dinosaurs, by using the persisted cache. But it will not raise a 'value' event, since the app has never executed that query while online.

        If the app were to request the last six items while offline, it would get 'child added' events for the four cached items straight away. When the device comes back online, the Firebase Realtime Database client synchronizes with the server and gets the final two 'child added' and the 'value' events for the app.
*/


public class WaterSupplier extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        //goOffline()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        FirebaseDatabase.getInstance().goOffline();
    }
}
