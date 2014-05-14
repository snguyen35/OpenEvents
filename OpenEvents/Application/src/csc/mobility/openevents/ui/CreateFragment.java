package csc.mobility.openevents.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.FacebookRequestError.Category;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.widget.UserSettingsFragment;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import csc.mobility.constant.CONSTANTS;
import csc.mobility.constant.EVENT_STATUS;
import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.Event;
import csc.mobility.helper.DateHelper;
import csc.mobility.helper.JSONSingleObjectDecode;
import csc.mobility.openevents.R;

public class CreateFragment extends BaseFragment implements OnDateSetListener, TimePickerDialog.OnTimeSetListener{
		
	private View rootView;	
	private static final String TAG = "CreateFragment";
	private EditText edEventName, edDetails, edLocation, edDate, edTime;
	private ImageButton btnDate, btnTime;
	private Switch btnSwitch;
		
	private static final String DATEPICKER_TAG = "datepicker";
	private static final String TIMEPICKER_TAG = "timepicker";    
	private static final int PICKER_PLACE_ACTIVITY = 1;	
	private GraphPlace selectedPlace = null;
	private static final String PLACE_KEY = "place";
	private boolean isGenFeed = true;
	private String place_id = "";
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub				
		rootView = inflater.inflate(R.layout.fragment_create, container, false);
				
		edEventName = (EditText) rootView.findViewById(R.id.edEventName);
		edDetails = (EditText) rootView.findViewById(R.id.edDetails);
		edLocation = (EditText) rootView.findViewById(R.id.edLocation);
		edDate = (EditText) rootView.findViewById(R.id.edDate);		
		edTime = (EditText) rootView.findViewById(R.id.edTime);		
		btnDate = (ImageButton) rootView.findViewById(R.id.btnDate);	
		btnTime = (ImageButton) rootView.findViewById(R.id.btnTime);
		btnSwitch = (Switch) rootView.findViewById(R.id.switch1);	
		
		addListenerOnButton();
		
		if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getChildFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getChildFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub		
	    inflater.inflate(R.menu.create_event, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub		
		switch (item.getItemId()) {
		case R.id.action_create:
			//handle event here
			if(validData())
				publishEvent();			
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
		String date = (month+1) + "/" + day + "/" + year;
		edDate.setText(date);	    
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		String time = hourOfDay + ":" + minute;
		edTime.setText(time);		
	}
			
	public void addListenerOnButton() {
		final Calendar calendar = Calendar.getInstance();
		 
		final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, 
		 												calendar.get(Calendar.YEAR), 
		 												calendar.get(Calendar.MONTH), 
		 												calendar.get(Calendar.DAY_OF_MONTH),false);
		 
		final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, 
		 												calendar.get(Calendar.HOUR_OF_DAY) ,
		 												calendar.get(Calendar.MINUTE), false, false);
		
		edLocation.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Session session = Session.getActiveSession();				
				if(event.getAction() == MotionEvent.ACTION_UP){
					if (session != null && session.isOpened()) {
						startPlacePickerActivity(PICKER_PLACE_ACTIVITY);
			        }else {
			        	UserSettingsFragment usf = new UserSettingsFragment();         
			            switchView(usf, "SETTING", false, null);
			        }
				}
				return false;
			}
		});
			
		edDate.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_UP){
					datePickerDialog.setVibrate(false);
					datePickerDialog.setYearRange(1985, 2036);
					datePickerDialog.show(getChildFragmentManager(), DATEPICKER_TAG);
				}
				return false;
			}
		});
					
		edTime.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_UP){
					timePickerDialog.setVibrate(false);
					timePickerDialog.show(getChildFragmentManager(), TIMEPICKER_TAG);
				}
				return false;
			}
		});		
			
		btnDate.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				datePickerDialog.setVibrate(false);
				datePickerDialog.setYearRange(1985, 2036);
				datePickerDialog.show(getChildFragmentManager(), DATEPICKER_TAG);
			}
		});
		
		btnTime.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				timePickerDialog.setVibrate(false);
				timePickerDialog.show(getChildFragmentManager(), TIMEPICKER_TAG);
			}
		});

		btnSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub		
				if(isChecked)
					isGenFeed = true;
				else
					isGenFeed = false;
			}
		});
	}
	
	
	public void publishEvent(){
		
		Session session = Session.getActiveSession();	
		
        List<String> permissions = session.getPermissions();
        if (!permissions.contains(CONSTANTS.PUB_CREATE_EVENT)) {                     
        	requestPublishPermissions(session);
            return;
        }          
        
        String eventStartTime = new DateHelper().toFBDateFormat(edDate.getText().toString(), edTime.getText().toString());
		Bundle params = new Bundle();
		String eventPrefix = "[CSCVN]";
		params.putString("name", eventPrefix + edEventName.getText().toString().trim());		
		params.putString("description", edDetails.getText().toString().trim());
		String location = "";
		if(edLocation.getText().toString().trim().equals(null))
			location = edLocation.getText().toString().trim();
		params.putString("location", location);
		if(!place_id.equals(""))
			params.putString("location_id", place_id);
		params.putString("start_time", eventStartTime);
		params.putBoolean("no_feed_story", isGenFeed);		
		
		/* make the API call */
		new Request(
		    session,
		    "me/events",
		    params,
		    HttpMethod.POST,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		            /* handle the result */			        	
		        	if (response.getError() != null) {
	                    handleError(response.getError());
	                }else{              		                	
	                	try {
							JSONObject object = response.getGraphObject().getInnerJSONObject();
							String eventId = object.getString("id");
							//Retrieve created event and send to DetailsFragment
							getCreatedEvent(eventId);							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	                	
	                }
		        }					
		    }
		).executeAsync();		
	}
	
	public void getCreatedEvent(String eventId) {
		// TODO Auto-generated method stub				
		Session session = Session.getActiveSession();
        if (session.getState() == SessionState.OPENED) {    
        	String fqlQuery = "SELECT eid, name, creator, description, start_time, location, venue"
    				+ " FROM event"
    				+ " WHERE eid = " + eventId;
    		Bundle params = new Bundle();
    		params.putString("q", fqlQuery);
    		
	    	new Request(
			    session,
			    "/fql",
			    params,
			    HttpMethod.GET,
			    new Request.Callback() {
			        public void onCompleted(Response response) {
			            /* handle the result */
			        	if (response.getError() != null) {
		                    handleError(response.getError());
		                }else{
		                	try {	
						        	JSONObject object = response.getGraphObject().getInnerJSONObject();		
						        	JSONObject eventObj = object.getJSONArray("data").getJSONObject(0);	        				        			        	

						        	JSONSingleObjectDecode jso = new JSONSingleObjectDecode();								        	
						        	Event event = jso.getEvent(eventObj);		
						        	event.setStatus(EVENT_STATUS.ATTENDING);
						        	
						        	Toast.makeText(getActivity(), "New event was created.", Toast.LENGTH_SHORT).show();
						        							        
						        	// Move to Details Fragment						        	
						        	switchToDetailView(FRAGMENT_NAME.CREATE, false, event);	
						        	
						    } catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		                }
			        }
			    }
			).executeAsync();   		 		    		   		
        }	
	}
		
	public boolean validData(){
		int error = 0;
		ArrayList<String> errorMessage = new ArrayList<String>();
		if(edEventName.getText().toString().trim().equals("") 
				|| edEventName.getText().toString().trim() == null){
			error ++;
			errorMessage.add("Enter event name");
		}

		if(edLocation.getText().toString().trim().equals("") 
				|| edLocation.getText().toString().trim() == null){
			error ++;
			errorMessage.add("Enter location");
		}
		if(edDate.getText().toString().trim().equals("") 
				|| edDate.getText().toString().trim() == null){
			error ++;
			errorMessage.add("Enter date");
		}
		
		if(error == 0){
			return true;
		}			
		else{
			String msg = "";
			for (String s : errorMessage) {
				msg += (s + "\n");				
			}			
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			return false;			
		}
	}
			
	private void requestPublishPermissions(Session session) {
        if (session != null) {
            Session.NewPermissionsRequest newPermissionsRequest = 
            		new Session.NewPermissionsRequest(this, CONSTANTS.PUBLISH_ACTIONS, CONSTANTS.PUB_CREATE_EVENT)
                    // demonstrate how to set an audience for the publish permissions,
                    // if none are set, this defaults to FRIENDS
                    .setDefaultAudience(SessionDefaultAudience.FRIENDS)
                    .setRequestCode(CONSTANTS.REAUTH_ACTIVITY_CODE);
            session.requestNewPublishPermissions(newPermissionsRequest);
        }
    }	    
		
	@Override
	public void handleError(FacebookRequestError error) {
		// TODO Auto-generated method stub
		super.handleError(error);
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
	}		
	
	 @Override
    public void onDestroy() {
        super.onDestroy();	              
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICKER_PLACE_ACTIVITY:
			selectedPlace = ((OpenEventsApplication) getActivity().getApplication()).getSelectedPlace();
			setPlaceText();
		    break;
		default:
		    break;
		}
	}
		
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);		
		if (selectedPlace != null) {
			outState.putString(PLACE_KEY, selectedPlace.getInnerJSONObject().toString());
        }
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		if(savedInstanceState != null){
			String place = savedInstanceState.getString(PLACE_KEY);
			if (place != null) {
		      try {
		          selectedPlace = GraphObject.Factory
		                  .create(new JSONObject(place), GraphPlace.class);
		          setPlaceText();               
		      } catch (JSONException e) {
		          Log.e(TAG, "Unable to deserialize place.", e);
		      }
			}
		}		
	}
	
	private void startPlacePickerActivity(int requestCode) {        
		OpenEventsApplication app = (OpenEventsApplication)getActivity().getApplication();
		app.setSelectedPlace(null);
		// Call place picker
		Intent intent = new Intent(getActivity(), PickerPlaceActivity.class);
		startActivityForResult(intent, requestCode);       
    }
	
	private void setPlaceText() {
        String results = null;
        if (selectedPlace != null) {        	
        	place_id = selectedPlace.getId();        	
        	results = selectedPlace.getName();
        }
        if (results == null) {
        	results = getResources().getString(R.string.string_select_location);
        }
        edLocation.setText(results);   
    }
}
