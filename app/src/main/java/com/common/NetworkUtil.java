package com.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {

	// Wifi 또는 3G 중 하나라도 연결되었는지 여부
	public static boolean isNetworkAvailable(Context context) {
		boolean bConnect = false;
		if(isWifiAvailable(context) || is3GAvailable(context)) {
			bConnect = true;
		}
		return bConnect;
	}
	
	
	public static String getIMEI(Context context) {
	    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    String IMEI = "";

	    try {
		    if (tm.getDeviceId() != null) {
		    	IMEI = tm.getDeviceId();
		    } else {
		    	IMEI = "Device ID is not available";
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
    	return IMEI;
	}
	
	public static String getIMSI(Context context) {
	    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    String IMSI = "";

	    try {
		    if (tm.getSubscriberId() != null) {
		    	IMSI = tm.getSubscriberId();
		    } else {
		    	IMSI = "Subscriber ID is not available";
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
    	return IMSI;
	}
	
	public static String getPhoneNumber(Context context) {
	    TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    String PhoneNumber = "";

	    try {
		    if (tm.getLine1Number() != null) {
		    	PhoneNumber = tm.getLine1Number();
		    } else {
		    	PhoneNumber = "Phone Number for Line 1 is not available";
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
    	return PhoneNumber;
	}
	
	// Wifi 가능 여부
	public static boolean isWifiAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean bConnect = false;
		try {
			if( cm == null ) return false;

			NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			bConnect = (ni.isAvailable() && ni.isConnected());

		} catch(Exception e) {
			return false;
		}

		return bConnect;
	}

	// 3G 가능 여부
	public static boolean is3GAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean bConnect = false;
		try {
			if( cm == null ) return false;
			NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			bConnect = (ni.isAvailable() && ni.isConnected());
		} catch(Exception e) {
			return false;
		}

		return bConnect;
	}
	

	// 네트워크 상태 체크 Android Demo Code
	public static String checkNetworkStatus(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		boolean isWifiAvail = ni.isAvailable();
		boolean isWifiConn = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileAvail = ni.isAvailable();
		boolean isMobileConn = ni.isConnected();
		
		return "WiFi\nAvail = [" + isWifiAvail + "]\nConn = ["
		+ isWifiConn + "]\nMobile\nAvail = [" + isMobileAvail
		+ "]\nConn = [" + isMobileConn + "]\n";
    }

}
