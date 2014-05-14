package csc.mobility.constant;

import android.net.Uri;

public class CONSTANTS {
	public static final String NOTIFICATION_ACTION = "csc.mobility.openevent.ui.Receiver.Notification";
	
	public static final String SPACE = " ";
	public static final String APP_ID = "296118947205054";
	public static final int REAUTH_ACTIVITY_CODE = 100;
	public static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");
	
	// List of Facebook PERMISSIONS
	// Read -permission
	public static final String READ_PUBLIC_PROFILE = "public_profile";
	public static final String READ_USER_EVENTS = "user_events";
	public static final String READ_USER_FRIENDS = "user_friends";
		
	
	
	//Publish -permission
	public static final String PUBLISH_ACTIONS = "publish_actions";
	public static final String PUBLISH_STREAM = "publish_stream";
	public static final String PUB_CREATE_EVENT = "create_event";
	public static final String PUB_RSVP_EVENT = "rsvp_event";
	public static final String PUB_MANAGE_FRIENDLISTS = "manage_friendlists";
	public static final String PUB_MANAGE_NOTIFICATIONS = "manage_notifications";	
		
	// KEY
	public static final String KEY_EVENT_ITEM = "EVENT_ITEM";
	public static final String KEY_EVENT_LIST = "EVENT_LIST";
	public static final String KEY_FRAGMENT_NAME="FRAGMENT_NAME";
	
	public static final String EVENT_PREFIX = "[CSCVN]";
	
	
}
