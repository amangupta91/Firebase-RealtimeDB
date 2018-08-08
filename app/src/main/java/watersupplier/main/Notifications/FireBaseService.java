package watersupplier.main.Notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import watersupplier.main.Main.First_Menu;
import watersupplier.main.Orders.Order_Search_ListActivity;
import watersupplier.main.R;

public class FireBaseService extends FirebaseMessagingService {

    private String TAG = "FireBaseService";
    Bitmap bitmap;
    private static int count = 0;
    public FireBaseService() {


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Aman From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Aman Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        //message will contain the Push Message
        String message = remoteMessage.getData().get("message");
        //imageUri will contain URL of the image to be displayed with Notification
        String imageUri = remoteMessage.getData().get("image");


        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.
//        String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");

        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUri);

        //below is notification with server msg & image
//        sendNotification(message, bitmap);

        //below is normal notification
//        sendNotification1(remoteMessage);

//Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(), remoteMessage.getData());


    }

    //This method is only generating push notification
    private void sendNotification(String messageTitle, String messageBody, Map<String, String> row) {
        PendingIntent contentIntent = null;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher1))
                .setSmallIcon(R.drawable.ic_launcher1)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(count, notificationBuilder.build());
        count++;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendNotification1(RemoteMessage remoteMessage) {


    }


    /**
     * Create and show a simple notification containing the received FCM message.
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void sendNotification(String messageBody, Bitmap image) {
//        Intent intent = new Intent(this, Order_Search_ListActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////        intent.putExtra("AnotherActivity", TrueOrFalse);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setLargeIcon(image)/*Notification icon image*/
//                .setSmallIcon(R.drawable.ic_launcher1)
//                .setContentTitle(messageBody)
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image))/*Notification with Image*/
//                .setAutoCancel(true)
//                .setColor(getColor(R.color.action_bar_color))
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
