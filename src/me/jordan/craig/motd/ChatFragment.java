package me.jordan.craig.motd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.google.android.gcm.GCMRegistrar;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Chat Fragment
 */
public class ChatFragment extends Fragment {
	private static final String TAG = "motd-chat";

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.chat_fragment, container, false);
	}

	public class ChatReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO: Add it to the list
		}
	}

	@Override
	public void onStart() {
		GCMRegistrar.checkDevice(getActivity());
		GCMRegistrar.checkManifest(getActivity());
		final String regId = GCMRegistrar.getRegistrationId(getActivity());
		if (regId.equals("")) {
			GCMRegistrar.register(getActivity(), GCMIntentService.SENDER_ID);
		} else {
			Log.v(TAG, "Already registered");
		}

		Button chat = (Button) getView().findViewById(R.id.send);
		chat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View view) {
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
							nameValuePairs.add(new BasicNameValuePair("from", "john")); // TODO: method of getting a name
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
							// TODO: Error message
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

		});

		IntentFilter filter = new IntentFilter();
		filter.setPriority(999);
		filter.addAction("me.jordan.craig.motd.CHAT_MESSAGE");
		getActivity().registerReceiver(new ChatReciever(), filter);

		// TODO: Open Chat from cache
	}

}
