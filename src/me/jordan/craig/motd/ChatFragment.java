package me.jordan.craig.motd;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gcm.GCMRegistrar;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Chat Fragment
 */
public class ChatFragment extends Fragment {
	private static final String TAG = "motd-chat";
	public ChatAdapter messageAdapter;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.chat_fragment, container, false);
	}

	public class ChatReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			ChatMessage message = ChatMessage.from(intent);
			Log.d("motd", "Got a message to show on screen");

			messageAdapter.add(message);
			messageAdapter.notifyDataSetChanged();

			abortBroadcast();
		}
	}

	public String name;

	public void sendMessage(final View view){
		final ProgressBar pb = (ProgressBar) getView().findViewById(R.id.progressBar);
		pb.setVisibility(View.VISIBLE);

		final EditText ed = (EditText) getView().findViewById(R.id.text);
		ed.setEnabled(false);
		view.setVisibility(View.GONE);



		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					HttpPost p = new HttpPost(GCMIntentService.PING_PONG_SERVER + "/ping");

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("message_from", name)); // TODO: method of getting a name
					nameValuePairs.add(new BasicNameValuePair("message", ed.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("tags", GCMIntentService.PING_TAG));
					p.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse r = new DefaultHttpClient().execute(p);
					if(r.getStatusLine().getStatusCode() != 200){
						throw new Exception("Non 200 response from server");
					}

					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ed.setText(""); // Empty it if sucessfull
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				// If we fail or not, it will still reactivate UI
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb.setVisibility(View.GONE);
						ed.setEnabled(true);
						view.setVisibility(View.VISIBLE);
					}
				});
			}

		}).start();
	}


	ChatReciever cr = new ChatReciever();

	@Override
	public void onStop(){
		super.onStop();

		getActivity().unregisterReceiver(cr);
	}

	@Override
	public void onStart() {
		super.onStart();

		GCMRegistrar.checkDevice(getActivity());
		GCMRegistrar.checkManifest(getActivity());
		final String regId = GCMRegistrar.getRegistrationId(getActivity());
		if (regId.equals("")) {
			GCMRegistrar.register(getActivity(), GCMIntentService.SENDER_ID);
		} else {
			Log.v(TAG, "Already registered");
			if(!PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("chat_ok", false)){
				Log.d("motd", "Reg failed");
				GCMRegistrar.register(getActivity(), GCMIntentService.SENDER_ID);
			}
		}

		Button chat = (Button) getView().findViewById(R.id.send);
		chat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View view) {

				if(name != null){
					sendMessage(view);
				} else{
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					name = sp.getString("name", null);

					if(name != null){
						sendMessage(view);
						return;
					}

					AlertDialog.Builder ab = new AlertDialog.Builder( getActivity() );
					final View v = LayoutInflater.from(getActivity()).inflate(R.layout.edit, null);

					ab.setTitle(R.string.choose_name);

					ab.setView(v);
					ab.setPositiveButton(android.R.string.ok, new android.content.DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {

							name = ((EditText) v.findViewById(R.id.val)).getText().toString();
							SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
							sp.edit().putString("name", name).commit();

							sendMessage(view);

							dialog.dismiss();

						}
					});
					ab.setNegativeButton(android.R.string.cancel, new android.content.DialogInterface.OnClickListener(){

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});

					ab.show();
				}
			}

		});

		IntentFilter filter = new IntentFilter();
		filter.setPriority(99);
		filter.addAction("me.jordan.craig.motd.CHAT_MESSAGE");
		getActivity().registerReceiver(cr, filter);

		messageAdapter = new ChatAdapter(getActivity());
		((ListView)getView().findViewById(R.id.listView)).setAdapter(messageAdapter);

		JSONArray cache = GCMIntentService.getChatCache(getActivity());
		for(int i = 0; i < cache.length(); i++){
			ChatMessage message = ChatMessage.from(cache.optJSONObject(i));
			messageAdapter.add(message);
		}
	}

	public static class ChatMessage{
		public static ChatMessage from(Intent intent){
			ChatMessage message = new ChatMessage();
			message.from = intent.getStringExtra("message_from");
			message.content = intent.getStringExtra("message");

			return message;
		}

		public static ChatMessage from(JSONObject from){
			ChatMessage message = new ChatMessage();
			message.from = from.optString("from");
			message.content = from.optString("content");

			return message;
		}

		public JSONObject toJSONObject(){
			JSONObject jo = new JSONObject();
			try{
				jo.put("from", from);
				jo.put("content", content);
			} catch(Exception e){} // Should never ever happen
			return jo;
		}

		public String from;
		public String content;
	}

	public class ChatAdapter extends ArrayAdapter<ChatMessage>{

		public ChatAdapter(Context context) {
			super(context, -1);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.chat_item, null);
			}

			ChatMessage message = getItem(position);

			TextView r = (TextView)convertView;
			r.setText(Html.fromHtml("<strong>" + message.from + ": </strong>" + message.content));

			return convertView;
		}
	}

}
