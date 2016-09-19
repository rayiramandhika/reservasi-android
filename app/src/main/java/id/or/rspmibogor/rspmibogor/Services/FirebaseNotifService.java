package id.or.rspmibogor.rspmibogor.Services;

/**
 * Created by iqbalprabu on 15/08/16.
 */
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

import id.or.rspmibogor.rspmibogor.DetailInbox;
import id.or.rspmibogor.rspmibogor.DetailOrder;
import id.or.rspmibogor.rspmibogor.DetailOrderOld;
import id.or.rspmibogor.rspmibogor.InboxActivity;
import id.or.rspmibogor.rspmibogor.MainActivity;
import id.or.rspmibogor.rspmibogor.PendaftaranActivity;
import id.or.rspmibogor.rspmibogor.R;

/**
 * Created by Belal on 5/27/2016.
 */

public class FirebaseNotifService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String activity = remoteMessage.getNotification().getClickAction();
        String id = remoteMessage.getData().get("id");
        String icon = remoteMessage.getNotification().getIcon();

        Log.d(TAG, "click_action: " + activity);
        Log.d(TAG, "id: " + id);

        PendingIntent mPendingIntent;
        Intent intent;

        if(activity != null)
        {
            if(activity.equals("open_detail_inbox")){

                if(id.equals(0)){

                    intent = new Intent(this, InboxActivity.class);

                } else {

                    intent = new Intent(this, DetailInbox.class);

                    Bundle b = new Bundle();
                    b.putString("id", id);
                    intent.putExtras(b);

                }

            }else if(activity.equals("open_detail_order")){

                if(id.equals(0)){

                    intent = new Intent(this, PendaftaranActivity.class);

                } else {

                    intent = new Intent(this, DetailOrder.class);

                    Bundle b = new Bundle();
                    b.putString("id", id);
                    intent.putExtras(b);

                }

            }else if(activity.equals("open_detail_order_old")){

                if(id.equals(0)){

                    intent = new Intent(this, PendaftaranActivity.class);

                }else {

                    intent = new Intent(this, DetailOrderOld.class);

                    Bundle b = new Bundle();
                    b.putString("id", id);
                    intent.putExtras(b);

                }

            }else {

                intent = new Intent(this, MainActivity.class);

            }
        }else {

            intent = new Intent(this, MainActivity.class);
        }


        mPendingIntent = PendingIntent.getActivities(getApplicationContext(), 100,
                new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);

        sendNotification(body, title, icon, mPendingIntent);
    }


    private void sendNotification(String messageBody, String title, String icon,  @Nullable PendingIntent pendingIntent) {


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String setTitle = "RS PMI Bogor";
        if(title != null) setTitle = title;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentTitle(setTitle);
        builder.setContentText(messageBody);
        builder.setAutoCancel(true);
        builder.setSound(defaultSoundUri);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }




}