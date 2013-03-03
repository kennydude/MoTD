package me.jordan.craig.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.StaticLayout;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
	// This will need changing depending on where the server is :(
	// NO TRAILING HASH OR IT WILL LITERALLY BRAKE EVERYTHING
	public static String SERVER = "http://192.168.0.1:3000";

	/**
	 * Helper function to check if the user has logged in
	 * @param c
	 * @return
	 */
	public static boolean checkForLogin(Context c){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
		return pref.contains("token");
	}
	
	/**
	 * Download image from internet
	 * @param url
	 * @return
	 */
	static public InputStream loadImage(String url) {
        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(15000);

            is = new BufferedInputStream(connection.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return is;
    }

	/**
	 * Get the Access Token we have saved on the user
	 * @param c
	 * @return
	 */
	public static String getAccessToken(Context c) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
		return pref.getString("token", null);
	}

	public static String readFile(InputStream stream){
		try{
			BufferedReader r = new BufferedReader(new InputStreamReader(stream));
			String rs = "", line = null;

			while( (line = r.readLine()) != null){
				rs += line + "\n";
			}

			r.close();
			return rs;
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void writeFile(OutputStream out, String content){
		try{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
			bw.write(content);
			bw.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static JSONArray remove(final int idx, final JSONArray from) {
		final List<JSONObject> objs = asList(from);
		objs.remove(idx);

		final JSONArray ja = new JSONArray();
		for (final JSONObject obj : objs) {
			ja.put(obj);
		}

		return ja;
	}

	public static List<JSONObject> asList(final JSONArray ja) {
		final int len = ja.length();
		final ArrayList<JSONObject> result = new ArrayList<JSONObject>(len);
		for (int i = 0; i < len; i++) {
			final JSONObject obj = ja.optJSONObject(i);
			if (obj != null) {
				result.add(obj);
			}
		}
		return result;
	}
}
