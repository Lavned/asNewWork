/**
 * 
 */
package com.com.shows.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author footman
 * 
 */
public class NetHelper {

	public static boolean IsHaveInternet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manger. getActiveNetworkInfo();
			return (info != null && info.isConnected() && info.isAvailable());
		} catch (Exception e) {
			return false;
		}
	}

}
