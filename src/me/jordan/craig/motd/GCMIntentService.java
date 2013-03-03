package me.jordan.craig.motd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import me.jordan.craig.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

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

	public static final Integer MAX_CACHE_SIZE = 20;

	public static JSONArray getChatCache(Context c){
		try{
			return new JSONArray( Utils.readFile(c.openFileInput("MOTD_CHAT_CACHE")) );
		} catch (Exception e){
			return new JSONArray();
		}
	}
	public static void setChatCache(JSONArray contents, Context c){
		try{
			Utils.writeFile(c.openFileOutput("MOTD_CHAT_CACHE", Context.MODE_PRIVATE), contents.toString());
		} catch (Exception e){ }
	}

    @Override
    protected void onMessage(Context context, Intent intent) {
	    Log.d("motd", "onMessage()");

	    ChatFragment.ChatMessage message = ChatFragment.ChatMessage.from(intent);

	    JSONArray ja = getChatCache(context);

	    // First trim the cache
		while( ja.length() > MAX_CACHE_SIZE ){
			ja = Utils.remove(0, ja);
		}

	    // Now add our item
	    ja.put( message.toJSONObject() );
		setChatCache(ja, context);

	    // Now prepare the intent for firing again
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
            HttpPost p = new HttpPost(PING_PONG_SERVER + "/pong/register");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("gcm", regId));
            nameValuePairs.add(new BasicNameValuePair("tags", PING_TAG));
            p.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse r = new DefaultHttpClient().execute(p);
            // TODO: Some real error handling but meh
            Log.d("re", "PingPongPush returned " + r.getStatusLine().getStatusCode());
	        if(  r.getStatusLine().getStatusCode() == 200 ){
		        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("chat_ok", true).commit();
	        }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onUnregistered(Context context, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
