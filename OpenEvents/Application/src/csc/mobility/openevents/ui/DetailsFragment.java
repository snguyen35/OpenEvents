package csc.mobility.openevents.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.FacebookRequestError;
import com.facebook.FacebookRequestError.Category;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import csc.mobility.constant.CONSTANTS;
import csc.mobility.constant.EVENT_STATUS;
import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.Event;
import csc.mobility.entity.EventMarker;
import csc.mobility.helper.AppSharedPreferences;
import csc.mobility.helper.ParseJSON;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.customview.CustomCalendarView;
import csc.mobility.service.GPSTracker;


public class DetailsFragment extends BaseFragment{
	
	private static final int PICKER_FRIENDS_ACTIVITY = 2;
	boolean pickFriendsWhenSessionOpened;
	
	private String eventId="", userId="";
	private static View rootView;	
	private ViewFlipper fliper;	
	private Spinner spinner;
	private List<String> spinnerListItem;
	private ArrayAdapter<String> spinnerAdapter;
	
	private CustomCalendarView ccv;
	private TextView tvEName, tvELocation, tvEDescription;	
	private Button btnJoinEvent, btnEditEvent, btnGetDirection, btnInviteFriend;
	private ImageView slider;
	private SlidingUpPanelLayout mLayout;	
	private boolean onLoad = false;
	
	private String fromFragment ="";
	private Event eventItem;
	
	private String mURL="";
	private MapView mMapView;
	private GoogleMap mMap; 
	private List<Marker> markers = new ArrayList<Marker>();
	private GPSTracker gps;
	private LatLng latlong;
		
	
	public static DetailsFragment newInstance(Event e){
		Bundle args = new Bundle();		
		args.putString(CONSTANTS.KEY_FRAGMENT_NAME, FRAGMENT_NAME.EVENT);
        args.putParcelable(CONSTANTS.KEY_EVENT_ITEM, e);
        DetailsFragment ef = new DetailsFragment();
        ef.setArguments(args);
		return ef;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		AppSharedPreferences prefs = new AppSharedPreferences();
		userId = prefs.getUserId(getActivity());
		
		Bundle args = getArguments();
		if (args!=null) {
			eventItem = (Event) args.getParcelable(CONSTANTS.KEY_EVENT_ITEM);
			eventId=eventItem.getId();
			fromFragment= args.getString(CONSTANTS.KEY_FRAGMENT_NAME);			
		}
		
		onLoad = true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
		rootView = inflater.inflate(R.layout.fragment_details, container, false);		
			
		ccv = (CustomCalendarView) rootView.findViewById(R.id.calendarView);
		tvEName = (TextView) rootView.findViewById(R.id.tvEName);
		tvELocation = (TextView) rootView.findViewById(R.id.tvELocation);
		tvEDescription = (TextView) rootView.findViewById(R.id.tvEDescription);
						
		btnEditEvent = (Button) rootView.findViewById(R.id.btn_Edit_Event);
		btnGetDirection = (Button) rootView.findViewById(R.id.btn_Get_Direction);
		btnInviteFriend = (Button) rootView.findViewById(R.id.btn_Invite_Friend);	
		btnJoinEvent=(Button) rootView.findViewById(R.id.btn_Join_Event);				
		spinner = (Spinner) rootView.findViewById(R.id.spinner_eventStt);
		slider = (ImageView) rootView.findViewById(R.id.slider);
		mLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);
		
		fliper =(ViewFlipper) rootView.findViewById(R.id.flipper);							
		//Change view					
		//If not join
		if (fromFragment.equals(FRAGMENT_NAME.SEARCH)) {
			fliper.setDisplayedChild(0);			
		}else{
			//If I am owner
			if(eventItem.getOwnerId().equals(userId)){
				fliper.setDisplayedChild(1);
			}else{
				// Other case
				setUpSpiner();
				
				if(eventItem.getStatus().equals(EVENT_STATUS.ATTENDING)){
					spinner.setSelection(0);							
				}else if(eventItem.getStatus().equals(EVENT_STATUS.FQL_UNSURE)){
					spinner.setSelection(1);					
				}else if(eventItem.getStatus().equals(EVENT_STATUS.DECLINED)){
					spinner.setSelection(2);					
				}else if(eventItem.getStatus().equals(EVENT_STATUS.NOT_REPLIED)){
					spinner.setSelection(3);					
				}
				fliper.setDisplayedChild(2);				
			}
		}
		
		// Gets the MapView from the XML layout and creates it
		mMapView = (MapView) rootView.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		setupMap();
	
		updateView();
		setControlListener();
		return rootView;
	}
		
	/*
	 * Activity life circle
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKER_FRIENDS_ACTIVITY:
                handleSelectedFriends(resultCode);
                break;
            default:
                Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
                break;
        }
    }
	   
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getActivity();
		// Update the display every time we are started.
        handleSelectedFriends(Activity.RESULT_OK);
	}
	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();	
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapView.onDestroy();
	}
		
	@Override
	public void updateView() {
		// TODO Auto-generated method stub			
		ccv.setDate(eventItem.getStart_time());   
		String eventName = eventItem.getName();
		if(!eventName.equals(""))
			eventName = eventName.replace(CONSTANTS.EVENT_PREFIX, "");		
 		tvEName.setText(eventName);
 		tvEDescription.setText(eventItem.getDescription());
 		tvELocation.setText("Location:" + eventItem.getLocation());	
 	}
		
		
	/**
     * Join event 
     */
    private void joinEvent(){
    	Session session = Session.getActiveSession();	

		//Require rsvp_event permission.
        List<String> permissions = session.getPermissions();
        if (!permissions.contains(CONSTANTS.PUB_RSVP_EVENT)) {                     
        	requestPublishPermissions(session);
            //return;
        }
      
        /* make the API call */
        new Request(
            session,
            eventId + "/attending",
            null,
            HttpMethod.POST,
            new Request.Callback() {
                public void onCompleted(Response response) {
                    /* handle the result */      
                	if (response.getError() != null) {
	                    handleError(response.getError());
	                }else{
	                	boolean result=false;
						try {						
							JSONObject object = response.getGraphObject().getInnerJSONObject();
							result = object.getBoolean("FACEBOOK_NON_JSON_RESULT");
							if(result){
								//Toast.makeText(getActivity(), "You're attending this event", Toast.LENGTH_SHORT).show();							
								//((MainActivity)getActivity()).switchContent(new EventPagerFragment(), CONSTANTS.FRAGMENT_EVENT_TITLE, false);
								onLoad = false;
								setUpSpiner();
								spinner.setSelection(0);
								fliper.setDisplayedChild(2);								
								Toast.makeText(getActivity(), "You have decided to join this event", Toast.LENGTH_SHORT).show();
							}
							else handleError(response.getError());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
	                }                					
                }
            }
        ).executeAsync();
    } 
		
	/**
	 * Invite friend
	 */
	private void startPickFriendsActivity() {
        if (ensureOpenSession()) {
            Intent intent = new Intent(getActivity(), PickerFriendActivity.class);            
            startActivityForResult(intent, PICKER_FRIENDS_ACTIVITY);
        } else {
            pickFriendsWhenSessionOpened = true;
        }
    }
	
	private void handleSelectedFriends(int resultCode) {     
        String friendIds = "";
        OpenEventsApplication app = (OpenEventsApplication)getActivity().getApplication();

        Collection<GraphUser> selection = app.getSelectedUsers();
        if (selection != null && selection.size() > 0) {
        	ArrayList<String> friendIdList = new ArrayList<String>();
            for (GraphUser user : selection) {
            	friendIdList.add(user.getId());
            }            
            friendIds = TextUtils.join(", ", friendIdList);
            
            //Send invite to friend
            inviteFriend(friendIds);
        }
//        else {           
//            Toast.makeText(getActivity(), "No friends selected", Toast.LENGTH_LONG).show();
//        }
    }
	
	private void inviteFriend(String friendIds){
		Session session = Session.getActiveSession();	
		
        List<String> permissions = session.getPermissions();
        if (!permissions.contains(CONSTANTS.PUB_CREATE_EVENT)) {                     
        	requestPublishPermissions(session);
            return;
        }
        
        Bundle params = new Bundle();
        params.putString("users", friendIds);
        /* make the API call */
        new Request(
            session,
            "/" + eventId + "/invited",
            params,
            HttpMethod.POST,
            new Request.Callback() {
                public void onCompleted(Response response) {
                    /* handle the result */
                	if (response.getError() != null) {
	                    handleError(response.getError());
	                }else{  
	                	Toast.makeText(getActivity(), "Your friends will be invited to this event.", Toast.LENGTH_SHORT).show();
	                }
                }
            }
        ).executeAsync();
	}
	// end invite
	
	/**
	 * Change event status	
	 */
	private void changeEventStatus(final String status){
		Session session = Session.getActiveSession();	

		//Require rsvp_event permission.
        List<String> permissions = session.getPermissions();
        if (!permissions.contains(CONSTANTS.PUB_RSVP_EVENT)) {                     
        	requestPublishPermissions(session);
            //return;
        }
        
		/* make the API call */
		new Request(
		    session,
		    "/" + eventId + "/" + status,
		    null,
		    HttpMethod.POST,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		            /* handle the result */
		        	if (response.getError() != null) {
	                    handleError(response.getError());
	                }else{  
			        	Toast.makeText(getActivity(), "Event's status was changed.", Toast.LENGTH_SHORT).show();
			        	if(eventItem.getStatus() != null){
			        		if(eventItem.getStatus().equals(EVENT_STATUS.NOT_REPLIED)){
				        		spinnerListItem.remove(3);
				        		spinnerAdapter.notifyDataSetChanged();
				        		eventItem.setStatus(EVENT_STATUS.FQL_UNSURE);
				        	}
			        	}	
	                }		        	
		        }
		    }
		).executeAsync();
	}
	    	
	/**
	 * Request create_event permission if need
	 * @param session
	 */
	private void requestPublishPermissions(Session session) {
        if (session != null) {
            Session.NewPermissionsRequest newPermissionsRequest = 
            		new Session.NewPermissionsRequest(this, CONSTANTS.PUBLISH_ACTIONS, CONSTANTS.PUB_CREATE_EVENT, CONSTANTS.PUB_RSVP_EVENT)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                   .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                   .setRequestCode(CONSTANTS.REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }
	
	public void setUpSpiner(){
		spinnerListItem = new ArrayList<String>();
		spinnerListItem.add(getString(R.string.event_going));
		spinnerListItem.add(getString(R.string.event_maybe));
		spinnerListItem.add(getString(R.string.event_not_going));
		if(eventItem.getStatus() != null){
			if(eventItem.getStatus().equals(EVENT_STATUS.NOT_REPLIED)){
				spinnerListItem.add(getString(R.string.event_not_replied));
			}
		}
		
		spinnerAdapter = new ArrayAdapter<String>(mActivity,
			R.layout.spinner_item, spinnerListItem);
		spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
	}
	
	private void setControlListener(){
				
		btnJoinEvent.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				joinEvent();				
			}			
		});
				
		btnEditEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getActivity() == null)
					return;									
				switchView(new EditFragment(), FRAGMENT_NAME.EDIT, true, eventItem);				
			}
		});
		
		btnGetDirection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				mURL=makeURL(latlong.latitude,latlong.longitude,Double.parseDouble(eventItem.getVenue().getLatitude()),Double.parseDouble(eventItem.getVenue().getLongitude()));
				connectAsyncTask cnn=new connectAsyncTask(mURL);
				cnn.execute();		
				//slider.performClick();
				
			}
		});
		
		btnInviteFriend.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startPickFriendsActivity();
			}
		});
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				// TODO Auto-generated method stub
				if(!onLoad){				
					String selectedText = parentView.getItemAtPosition(position).toString();				
					if(selectedText.equals(getString(R.string.event_going))){						
						changeEventStatus(EVENT_STATUS.ATTENDING);
					}else if(selectedText.equals(getString(R.string.event_maybe))){						
						changeEventStatus(EVENT_STATUS.GRAPH_MAYBE);
					}else if(selectedText.equals(getString(R.string.event_not_going))){						
						changeEventStatus(EVENT_STATUS.DECLINED);
					}					
				}
				onLoad = false;							
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// TODO Auto-generated method stub				
			}
		});
	
		mLayout.setPanelSlideListener(new PanelSlideListener() {			
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public void onPanelExpanded(View panel) {
				// TODO Auto-generated method stub
				slider.setImageResource(R.drawable.slide_down);				
			}
			
			@Override
			public void onPanelCollapsed(View panel) {
				// TODO Auto-generated method stub
				slider.setImageResource(R.drawable.slide_up);				
			}
			
			@Override
			public void onPanelAnchored(View panel) {
				// TODO Auto-generated method stub
				
			}
		});
	
	}
	
	private boolean ensureOpenSession() {
        if (Session.getActiveSession() == null ||
                !Session.getActiveSession().isOpened()) {
            Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChanged(session, state, exception);
                }
            });
            return false;
        }
        return true;
    }

    private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
        if (pickFriendsWhenSessionOpened && state.isOpened()) {
            pickFriendsWhenSessionOpened = false;
            startPickFriendsActivity();
        }
    }
    
	/**
	 * Handle error from facebook response
	 */
	@Override
	public void handleError(FacebookRequestError error) {
		// TODO Auto-generated method stub		
		if(error.getCategory() == Category.PERMISSION){
			// request the read permission
			DialogInterface.OnClickListener listener = null;
	        String dialogBody = null;
	        
            dialogBody = getString(R.string.error_permission);
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {                            
                    requestPublishPermissions(Session.getActiveSession());
                }
            };
            
            new AlertDialog.Builder(getActivity())
            .setPositiveButton(R.string.error_dialog_button_text, listener)
            .setTitle(R.string.error_dialog_title)
            .setMessage(dialogBody)
            .show();
		}	
		super.handleError(error);
	}		

	/*
	 * Get direction - Begin
	 */	
	public void setupMap(){
		// Do a null check to confirm that we have not already instantiated the map				
		if (mMap == null) {
			// Gets to GoogleMap from the MapView and does initialization stuff
			mMap = mMapView.getMap();

			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				MapsInitializer.initialize(rootView.getContext());
				
				gps=new GPSTracker(getActivity().getApplicationContext());
				findCurrentLocation();
				putMyMarker();			
				
				mMap.setMyLocationEnabled(true);
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				// mMap.getUiSettings().setMyLocationButtonEnabled(false);
				
				mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
					@Override
					public void onCameraChange(CameraPosition arg0) {
						// TODO Auto-generated method stub
						resetCamera();
						mMap.setOnCameraChangeListener(null);
					}
				});				
			}
		}
	}

	public void resetCamera(){
		int padding = 60; 
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(getMarkerLatLngBounds(), padding);
		mMap.moveCamera(cu);
	}
	
	public void findCurrentLocation(){
		if(gps.canGetLocation()){			
			gps.getLocation();
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            latlong=new LatLng(latitude,longitude);   	    
		}
		else{           
            gps.showSettingsAlert();
            }	
	}
	
	public void putMyMarker(){
		EventMarker eventMarker=new EventMarker(getActivity().getApplicationContext());
		
		Marker marker=mMap.addMarker(new MarkerOptions()
	        .snippet(eventItem.getLocation())
	        //.icon(BitmapDescriptorFactory.fromBitmap(eventMarker.getMarker("You are Here")))
	          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_source))
	        .position(latlong));
		markers.add(marker);
		
		marker=mMap.addMarker(new MarkerOptions()
        //.title(eventItem.getName())
        //.snippet(eventItem.getLocation())
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_des))
        .position(new LatLng(Double.parseDouble(eventItem.getVenue().getLatitude()),Double.parseDouble(eventItem.getVenue().getLongitude()))));
		markers.add(marker);
	}
		
	public LatLngBounds getMarkerLatLngBounds(){
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : markers) {
		    builder.include(marker.getPosition());
		}
		return builder.build();
	}
	
	public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
 }
	
	public void drawPath(String  result, boolean withSteps) {

	    try {
	            //Tranform the string into a json object
	           final JSONObject json = new JSONObject(result);
	           JSONArray routeArray = json.getJSONArray("routes");
	           JSONObject routes = routeArray.getJSONObject(0);
	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	           String encodedString = overviewPolylines.getString("points");
	           List<LatLng> list = decodePoly(encodedString);

	           for(int z = 0; z<list.size()-1;z++){
	                LatLng src= list.get(z);
	                LatLng dest= list.get(z+1);
	                Polyline line = mMap.addPolyline(new PolylineOptions()
	                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
	                .width(14)
	                .color(Color.BLUE).geodesic(true));
	            }
	           
	           if(withSteps)
	           {
	        	   JSONArray arrayLegs = routes.getJSONArray("legs");
	        	   JSONObject legs = arrayLegs.getJSONObject(0);
	        	   JSONArray stepsArray = legs.getJSONArray("steps");
	        	   //put initial point

	        	   for(int i=0;i<stepsArray.length();i++)
	        	   {
	        		   Step step = new Step(stepsArray.getJSONObject(i));
	        		   Marker markerTemp=mMap.addMarker(new MarkerOptions()
		         		.position(step.location)
		         		.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_trans)));
	        		   markers.add(markerTemp);

	        	   }
	           }
	    } 
	    catch (JSONException e) {

	    }
	} 
	private class Step
	 {
		 public String distance;
		 public LatLng location;
		 public String instructions;

		 Step(JSONObject stepJSON)
		 {
			 JSONObject startLocation;
			try {

				distance = stepJSON.getJSONObject("distance").getString("text");
				startLocation = stepJSON.getJSONObject("start_location");
				location = new LatLng(startLocation.getDouble("lat"),startLocation.getDouble("lng"));				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }		 
	 }
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}	
	
	private class connectAsyncTask extends AsyncTask<Void, Void, String>{
	    private ProgressDialog progressDialog;
	    String url;
	    connectAsyncTask(String urlPass){
	        url = urlPass;
	    }
	    @Override
	    protected void onPreExecute() {
	        // TODO Auto-generated method stub
	        super.onPreExecute();
	        progressDialog = new ProgressDialog(getActivity(),R.style.CustomDialog);
		    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		    progressDialog.setIndeterminate(true);
		    progressDialog.setMessage("Loading route. Please wait.");
		    progressDialog.show();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	        ParseJSON jParser = new ParseJSON();
	        String json = jParser.getJSONFromUrl(url);
	        return json;
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        progressDialog.hide();        
	        if(result!=null){
	            drawPath(result, true);
	           resetCamera();	           
	        }
	        mLayout.expandPane();
	    }
	}
	/*
	 * Get direction - End
	 */
	



}
