package me.jordan.craig.motd;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Used when in the background a message has been received
 */
public class BackgroundChatReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationCompat.Builder notify = new NotificationCompat.Builder(context);
		notify.setContentTitle(intent.getStringExtra("from"));
		notify.setContentText(intent.getStringExtra("message"));
		notify.setSmallIcon(R.drawable.ic_stat_message);

		Intent i = new Intent(context, BaseActivity.class);
		i.putExtra("page", "chat");
		notify.setContentIntent(PendingIntent.getActivity(context, 0, i, 0));

		((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, notify.getNotification());
	}

}
