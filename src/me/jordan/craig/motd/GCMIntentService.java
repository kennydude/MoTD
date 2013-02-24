package me.jordan.craig.motd;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Processes GCM messages for chat mostly.
 */
public class GCMIntentService extends GCMBaseIntentService {
	// This is the LandOfTechnology push server information
	// basically, it's a really simple service which will work fine for
	// what we want :)
    public static final String SENDER_ID = "750992842025";
    public static final String PING_TAG = "marchofthedroids";
    public static final String PING_PONG_SERVER = "http://lot.aws.af.cm";

    @Override
    protected void onMessage(Context context, Intent intent) {
	    // TODO: Add to chat cache here as every reciever will need to anyway


	    intent.setAction("me.jordan.craig.motd.CHAT_MESSAGE");
	    intent.setPackage(null);
	    intent.setComponent(null);

	    // We ask for an ordered broadcast so our activity pinches it first
        context.sendOrderedBroadcast(intent, "me.jordan.craig.motd.permission.C2D_MESSAGE");
    }

    @Override
    protected void onError(Context context, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        try{
            HttpPost p = new HttpPost(PING_PONG_SERVER + "pong/register");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("gcm", regId));
            nameValuePairs.add(new BasicNameValuePair("tags", PING_TAG));
            p.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse r = new DefaultHttpClient().execute(p);
            // TODO: Some real error handling but meh
            Log.d("re", "PingPongPush returned " + r.getStatusLine().getStatusCode());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onUnregistered(Context context, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
