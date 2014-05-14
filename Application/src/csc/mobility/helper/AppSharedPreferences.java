package csc.mobility.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppSharedPreferences {
	
	public final String USER_ID_KEY = "USERID";
	
	public void setUserId(Context context, String value){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString(USER_ID_KEY, value);
		editor.commit();
	}
	
	public String getUserId(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString(USER_ID_KEY, null);		
	}
	
}
