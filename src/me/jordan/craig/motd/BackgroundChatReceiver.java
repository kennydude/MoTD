package me.jordan.craig.motd;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Used when in the background a message has been received
 */
public class BackgroundChatReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("motd", "background chat");

		NotificationCompat.Builder notify = new NotificationCompat.Builder(context);
		notify.setContentTitle(intent.getStringExtra("message_from"));
		notify.setContentText(intent.getStringExtra("message"));
		notify.setSmallIcon(R.drawable.ic_stat_message);

		notify.setAutoCancel(true);
		notify.setWhen( System.currentTimeMillis() );

		Intent i = new Intent(context, BaseActivity.class);
		i.putExtra("page", 2);
		notify.setContentIntent(PendingIntent.getActivity(context, 0, i, 0));

		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notify.getNotification());
	}

}
