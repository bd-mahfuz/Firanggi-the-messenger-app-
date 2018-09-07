package com.everest.mahfuz.firanggi.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.everest.mahfuz.firanggi.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by mahfuz on 8/21/18.
 */

public class FirebaseMessagingServiceTest extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentTitle("Friend REquest");
        builder.setContentText("You got a new freind request");

        int notificationId = (int) System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(notificationManager);
*/
        notificationManager.notify(0, builder.build());




    }

/*    @TargetApi(28)
    private void createChannel(NotificationManager notificationManager) {
        String name = "FileDownload";
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }*/

}
