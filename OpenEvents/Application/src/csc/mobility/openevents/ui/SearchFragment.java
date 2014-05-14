package csc.mobility.openevents.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.Event;
import csc.mobility.entity.EventAdapter;
import csc.mobility.entity.Venue;
import csc.mobility.helper.ParseJSON;
import csc.mobility.helper.findOffSetFromMetter;
import csc.mobility.openevents.R;
import csc.mobility.service.GPSTracker;

public class SearchFragment extends BaseFragment {
	
	private ArrayList <Event> eventList= new ArrayList <Event>();
	Intent intent;
	private EventAdapter eventAdapter=new EventAdapter();
	private LatLng latlong;
	private GoogleMap map; 
    private GPSTracker gps;
    private Circle circle;
    private int radius=1000;
    protected int zoom=13;
    private RadioGroup radiusGroup;
    //private EventMarker eventMarker;
    private static View rootView;
    private Venue venue;
    private ProgressDialog progressDialog;
    private Hashtable<Marker, String> markers;
    private Hashtable<Marker, Bitmap> imageCache;
    private Marker markerTemp=null; //track the clicked marker
    private Location eventLocation;
    AQuery aq;
   
    int screenWidth;
    boolean reload; //reload to show if it's back from other fragment
   
    @Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		reload=false;
	}
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		setHasOptionsMenu(true);
					 
		if (rootView != null) {
	        ViewGroup parent = (ViewGroup) rootView.getParent();
	        if (parent != null)
	            parent.removeView(rootView);
	    }
	    try {
	        rootView = inflater.inflate(R.layout.fragment_search, container, false);
	    } catch (InflateException e) {	        
	    }
	   	
	   
	   
	   if(!reload){
		markerTemp=null;
	    markers = new Hashtable<Marker, String>();
        imageCache=new Hashtable<Marker,Bitmap>();
		radiusGroup=(RadioGroup) rootView.findViewById(R.id.radio_group_list_selector);
		//radiusGroup.check(R.id.btn1km);
	    map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if (map==null) {
			 GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
			 return rootView;
		}
		//else{		
		gps=new GPSTracker(getActivity().getApplicationContext());
		aq=new AQuery(getActivity());
		
		screenWidth=mActivity.getScreenWidth();
		zoom= calculateZoomLevel(screenWidth, (radius+600)*16f);		
		findMyLocation();
		drawCircle(latlong);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong , zoom)); 
		
		getEvent();
		showProgressDialog();
		
    	 
		radiusGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
                case R.id.btn1km:
                	radius=1000;  
                	zoom= calculateZoomLevel(screenWidth, (radius+600)*16f);
                    markerTemp=null;   
                   break;

                case R.id.btn2km:
                	radius=2000;  
                	zoom= calculateZoomLevel(screenWidth, (radius+600)*15f);
                	markerTemp=null;
                	break;

                case R.id.btn5km: 
                	radius=5000;
                	//zoom=13; 
                	zoom= calculateZoomLevel(screenWidth, (radius+600)*15f);
                	markerTemp=null;
                	break;
            }	
				map.clear();
                drawCircle(latlong); 
                showProgressDialog();
                getEvent();                
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong , zoom));     
			}			
		});
		
				
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

		        @Override
		        public void onInfoWindowClick(Marker arg0) {
		        	
		        	reload=true;
		        	int index=0;
		        	String eTitle=arg0.getId();
		        	eTitle.replace("\n","");
		        	for (int i=0; i<eventList.size();i++){
		        		if (eventList.get(i).getName().equals(eTitle)) {
		        			index=i;
		        			break;
		        		}
		        	}		       
		    		
		    		switchToDetailView(FRAGMENT_NAME.SEARCH, true, eventList.get(index));			           
		        }
		    });		
		 
		 map.setOnMarkerClickListener(new OnMarkerClickListener(){

			@Override
			public boolean onMarkerClick(Marker marker) {
				
				if (marker.getTitle().equals("We are Here!")) 
					return false;
				if (markerTemp!=null) markerTemp.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
				markerTemp=marker;
				marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_base_marker));
								
				return false;
			}		 
		 });
		 map.setOnMapClickListener(new OnMapClickListener(){

			 //set marker to default
			@Override
			public void onMapClick(LatLng arg0) {
				
				if (markerTemp!=null) markerTemp.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
			}
			 
		 });
	
		 map.setInfoWindowAdapter(new myInfoWindowAdapter());
		 }
	  // }
	   else mActivity.getActionBar().setTitle(FRAGMENT_NAME.SEARCH);
	return rootView;
	}	
	
@Override	
	public boolean onOptionsItemSelected(MenuItem item) {		
		Fragment newContent=null;
		switch (item.getItemId()) {
		case R.id.action_show_list:
				
			newContent=new EventFragmentfromMap();			
			if(newContent != null){
				reload=true;
				Bundle args = new Bundle();
				args.putParcelableArrayList("EVENT_LIST", eventList);	
				
				newContent.setArguments(args);
				mActivity.switchView(newContent, FRAGMENT_NAME.SEARCH_LIST, true);				
			}				   
		}
		return super.onOptionsItemSelected(item);
	}

	public void findMyLocation(){
		map.clear();
		if(gps.canGetLocation()){			
			gps.getLocation();
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            latlong=new LatLng(latitude,longitude);
            venue=new Venue(String.valueOf(latitude),String.valueOf(longitude));
                              
		}
		else{           
            gps.showSettingsAlert();
            }	
	}	
	
	private int calculateZoomLevel(int screenWidth, float radius) {
	    double equatorLength = 40075004; // in meters
	    double widthInPixels = screenWidth;
	    double metersPerPixel = equatorLength / 256;
	    int zoomLevel = 1;
	    while ((metersPerPixel * widthInPixels) > radius) {
	        metersPerPixel /= 2;
	        ++zoomLevel;
	    }
	    Log.i("ADNAN", "zoom level = "+zoomLevel);
	    return zoomLevel;
	}

	public void findEventLocation(){
		double eLat;
        double eLng;
        LatLng eLatLng;
        markers.clear();  
        for(Event event:eventList){
 		  	eLat=Double.parseDouble(event.getVenue().getLatitude());
 		  	eLng=Double.parseDouble(event.getVenue().getLongitude());
 		  	
 		  
 		    String eLocation=event.getLocation();
 		    String eName=event.getName();
 		    String eID=event.getId();
 		    
 		    eventLocation=new Location("Event Location");
         	eventLocation.setLatitude(eLat);
 		  	eventLocation.setLongitude(eLng);
 		  	
 		  	//if (getDistance(gps.getLocation(),eventLocation)<=radius){
 		  		Bitmap preset = aq.getCachedImage(event.getpicSmall());
 		  			  
 		    //check if eName size>40 to multiline textview
 		  	if(eName.length()>40){
 		  	   	int index=eName.indexOf(" ", 40);
 		  	   	if(index>0){
 		  	   	StringBuilder sb=new StringBuilder(eName);
 		  	   	eName=sb.insert(index, "\n").toString();
 		  	   	}
 		  	    }  		  	    	
 		  	eLatLng=new LatLng(eLat,eLng);
 		  	
 		  	Marker marker=map.addMarker(new MarkerOptions()
 		        .title(eName)  		        
 		        .snippet(eLocation)  		        
 		        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
 		        .position(eLatLng));   	
 		  	
 			markers.put(marker,eID);
 			if (preset!=null) 
 			    imageCache.put(marker, preset);
 		 // }
        }
        progressDialog.dismiss();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) 
	{
		 super.onConfigurationChanged(newConfig);
	     reload=true;
	  
	}
	public void getEvent(){
		
        if (eventList!=null) eventList.clear();
        /*String fqlQuery="SELECT eid, name, creator, description, start_time, venue, location, pic_small FROM event " +
        		"WHERE creator IN (SELECT page_id FROM place WHERE distance(latitude, longitude, '" +
                		Double.toString(latlong.latitude) +
                		"', '" +
                		Double.toString(latlong.longitude) +
                		"') < " +
                		Integer.toString(radius) +
                		" limit 0,15000) and (start_time>now() or end_time > now()) and strlen(venue.longitude)<>0 and strlen(venue.latitude)<>0 ORDER BY start_time desc limit 0,1500";*/
       //identify offset from distance
        
        LatLng offset=findOffSetFromMetter.convert(latlong.latitude, latlong.longitude, radius);
        double offsetx=offset.latitude;
        double offsety=offset.longitude;
        String fqlQuery="SELECT eid, name, creator, description, start_time, venue, location, pic_small FROM event " +
        		"where contains  ('[CSCVN]') and  strlen(venue.longitude)<>0 and strlen(venue.latitude)<>0 and " +
        		"venue.longitude < '" +
        		Double.toString(latlong.longitude +offsety) +
        		"' AND venue.latitude < '" +
        		Double.toString(latlong.latitude  + offsetx)+
        		"' AND venue.longitude >'" +
        		Double.toString(latlong.longitude -offsety) +
        		"' AND venue.latitude >'" +
        		Double.toString(latlong.latitude  - offsetx) +
        		"' AND (start_time>now() or end_time>now()) and creator<>me()";          
        
              
		Bundle params = new Bundle();
        params.putString("q", fqlQuery);
        Session session = Session.getActiveSession();
        Request request = new Request(session,
            "/fql",                         
            params,                         
            HttpMethod.GET,                 
            new Request.Callback(){         
                public void onCompleted(Response response) {                        
                    try {
                    	    eventAdapter.setEvenList(ParseJSON.parse(response));
							eventList=eventAdapter.getEvenList();
							
							findEventLocation();							
							
						} catch (JSONException e) {								
							e.printStackTrace();
						}       
        
                }                  
        }); 
        Request.executeBatchAsync(request);           		
	}	
	
   	 private void showProgressDialog() {
		    progressDialog = new ProgressDialog(mActivity,R.style.CustomDialog);
		    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		    progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		    progressDialog.setIndeterminate(true);
		    progressDialog.setMessage("Loading Events. Please wait.");
		    progressDialog.show();
		}
	public void drawCircle(LatLng position){
	    int strokeColor = 0xffff0000; //red outline
	    int shadeColor = 0x44ff0000; //opaque red fill

	    CircleOptions circleOptions = new CircleOptions().center(position).radius(radius+600).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(3);
	    circle = map.addCircle(circleOptions);

	    //MarkerOptions markerOptions = new MarkerOptions().position(position).title(getAddress(position));
	    MarkerOptions markerOptions = new MarkerOptions().position(position).title("We are Here!");
	    map.addMarker(markerOptions);
	   
	}
	public double getDistance(Location A, Location B){
		return A.distanceTo(B);
	}
	public String getAddress(LatLng latlng) {
		String strAddress="We are Here";
		try {
            Geocoder geocoder;
            List<Address> addresses = null;
            double latitude=latlng.latitude;
            double longitude=latlng.longitude;
            geocoder = new Geocoder(getActivity().getApplicationContext());
            if (latitude != 0 || longitude!= 0) {
                addresses = geocoder.getFromLocation(latitude , longitude, 1);
                        strAddress = addresses.get(0).getAddressLine(0) + addresses.get(0).getAddressLine(2) + addresses.get(0).getAddressLine(3);
            
            } 
            
        } catch (Exception e) {
            e.printStackTrace();            
        }
		return strAddress;
    }
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
				
	    inflater.inflate(R.menu.search, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	    }
	
	
	
	public double getRadioButtonCheck(){
			 
		// Returns an integer which represents the selected radio button's ID
		int selected = radiusGroup.getCheckedRadioButtonId();
		RadioButton b = (RadioButton) rootView.findViewById(selected);
		// Now you can get the text or whatever you want from the "selected" radio button		
		return Double.parseDouble(b.getText().toString());
	}
	
	private class myInfoWindowAdapter implements InfoWindowAdapter {

		private final View view;
		private MainActivity main;
		final ImageView image;
		public myInfoWindowAdapter() {			
			
			main = (MainActivity) getActivity();
			view=main.getLayoutInflater().inflate(R.layout.mywindowinfo,null);   
			image= ((ImageView) view.findViewById(R.id.badge));			   
		    }
		@Override
		public View getInfoContents(Marker arg0) {
					  
	            return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {  
	             
				
	            String eventId=markers.get(marker);
	            String imageURL="";
	            for(Event e:eventList){
	            	if (e.getId().equals(eventId)) {
	            		imageURL=e.getpicSmall();	            		
	            		AQuery aq=new AQuery(getActivity());
		            	aq.id(image).image(imageURL);          
	            	}
	            }          
	            
	            if (imageURL=="")
	            image.setImageResource(R.drawable.ic_launcher);
	            else {	
	            	aq.id(image).image( imageURL,false, false, 0, 0, imageCache.get(marker), AQuery.FADE_IN);	    
	            	//aq.id(image).image(imageURL); 
	            }
	            
	            final TextView titleUi = ((TextView) view.findViewById(R.id.title));
	            titleUi.setText(marker.getTitle());
	            
	            final TextView snippetUi = ((TextView) view
	                    .findViewById(R.id.snippet));
	         
	                snippetUi.setText(marker.getSnippet());          
	 
	            return view;
	        }	
		
	}
	 	
	}
	



