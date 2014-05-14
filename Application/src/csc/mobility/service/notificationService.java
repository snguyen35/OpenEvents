package csc.mobility.service;

import org.json.JSONArray;
import org.json.JSONException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Request.Callback;
import com.facebook.Response;
import com.facebook.Session;

import csc.mobility.constant.CONSTANTS;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class notificationService extends Service{
	Session session = null;
	Callback notificationCallback = null;
	Boolean isProcessing = false;
	//Messenger messenger = new Messenger(new IncomeHandler());
	
	private String responseData = "...";
	
	public String getResponseData() {
		return responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {		
		Toast.makeText(getApplicationContext(), "create..", Toast.LENGTH_LONG).show();
				
		notificationCallback = new Request.Callback() {
	        public void onCompleted(Response response) {	        		        		        
	        	Toast.makeText(getApplicationContext(), responseData, Toast.LENGTH_LONG).show();
	        		
	        	isProcessing = false;
	        	try {
	        		
	        		JSONArray albumArr = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
	        		//Sample data: {"to":{"id":"1818834805","name":"Dang"},"id":"notif_1818834805_52591621","application":{"id":"210831918949520","namespace":"candycrush","name":"Candy Crush Saga"},"title":"Candy Crush Saga: Hoa Tim sent you a request.","link":"http:\/\/apps.facebook.com\/candycrush\/?fb_source=notification&request_ids=686285078077615%2C416623958481547&ref=notif&app_request_type=user_to_user","unread":1,"object":{"id":"210831918949520","namespace":"candycrush","name":"Candy Crush Saga"},"from":{"id":"100000080025376","name":"Hoa Tim"},"created_time":"2014-03-09T08:04:31+0000","updated_time":"2014-03-10T01:03:37+0000"}
	        		
	        		Intent broadcastIntent = new Intent();
	     	        broadcastIntent.setAction(CONSTANTS.NOTIFICATION_ACTION);
	     	        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
	     	        
	     	        broadcastIntent.putExtra("notification_content", albumArr.toString());	        
	     	        broadcastIntent.putExtra("notification_count", albumArr.length());
	     	        sendBroadcast(broadcastIntent);
	        	}				
	        	catch(NullPointerException ex)
	        	{
	        		
	        	}
	        	//checkNotification();
	        	catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    };
		
		//super.onCreate();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(getApplicationContext(), "Intent service desctroy..", Toast.LENGTH_LONG).show();
		//TODO
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(getApplicationContext(), "start..", Toast.LENGTH_LONG).show();
		checkNotification();
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void checkNotification()
	{	
		if(isProcessing) return;
		Toast.makeText(getApplicationContext(), "check notification..", Toast.LENGTH_LONG).show();
		
		isProcessing = true;
		session = Session.getActiveSession();
    	if(session == null || session.getPermissions() == null || session.getPermissions().size() <= 1)    	    
    		return;
    	
		new Request(
			    session,
			    "me/notifications",			    
			    null, HttpMethod.GET, notificationCallback			    
			).executeAsync();
	}
	
	/*
	public class IncomeHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
				case 123:										
					
					break;
				case 456:
					
					break;				
			}
			super.handleMessage(msg);
		}
		
	}*/
}
