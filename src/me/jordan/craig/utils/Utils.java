package me.jordan.craig.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
	
}
