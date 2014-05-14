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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
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

public class EditFragment extends BaseFragment implements OnDateSetListener, TimePickerDialog.OnTimeSetListener{

	private View rootView;	
	private static final String TAG = "EditFragment";
	private EditText edEventName, edDetails, edLocation, edDate, edTime;
	private ImageButton btnDate, btnTime;
	Button btnCancelEvent;
	
	private static final String DATEPICKER_TAG = "datepicker";
	private static final String TIMEPICKER_TAG = "timepicker";    
	private static final int PICKER_PLACE_ACTIVITY = 1;	
	private GraphPlace selectedPlace = null;
	private static final String PLACE_KEY = "place";	
	private String place_id = "";
	private Event eventItem;
	private String eventId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		Bundle args = getArguments();
		if (args!=null) {
			eventItem = (Event) args.getParcelable(CONSTANTS.KEY_EVENT_ITEM);
			eventId=eventItem.getId();			
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub				
		rootView = inflater.inflate(R.layout.fragment_edit, container, false);
						
		edEventName = (EditText) rootView.findViewById(R.id.edEventName);
		edDetails = (EditText) rootView.findViewById(R.id.edDetails);
		edLocation = (EditText) rootView.findViewById(R.id.edLocation);
		edDate = (EditText) rootView.findViewById(R.id.edDate);		
		edTime = (EditText) rootView.findViewById(R.id.edTime);	
		btnDate = (ImageButton) rootView.findViewById(R.id.btnDate);					
		btnTime = (ImageButton) rootView.findViewById(R.id.btnTime);
		btnCancelEvent = (Button) rootView.findViewById(R.id.btn_Cancel_Event);
		
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
		
		updateView();
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub		
	    inflater.inflate(R.menu.edit_event, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub		
		switch (item.getItemId()) {
		case R.id.action_update:
			//handle event here
			if(validData())
				updateEvent();			
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
	
	@Override
	public void updateView() {
		// TODO Auto-generated method stub			
		String eventName = eventItem.getName();
		if(!eventName.equals(""))
			eventName = eventName.replace(CONSTANTS.EVENT_PREFIX, "");		
		edEventName.setText(eventName);
 				
		edDetails.setText(eventItem.getDescription());
		edLocation.setText(eventItem.getLocation());	
		
		String date = new DateHelper().to24HoursFormat(eventItem.getStart_time());		
		String[] result = date.split("-");
		edDate.setText(result[0]);
		edTime.setText(result[1]);
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
		
		btnCancelEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogInterface.OnClickListener dlogClickListener = new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
							// Yes button clicked
							case DialogInterface.BUTTON_POSITIVE:
									cancelEvent();
								break;
			
							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked 
								// Do nothing & return
								break;
						}
					}
				};
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Are you sure you want to cancel this event ?")
						.setPositiveButton("Yes", dlogClickListener)
						.setNegativeButton("No", dlogClickListener).show();
			}
		});
		
	}
	
	/**
	 * Cancel event
	 */
	private void cancelEvent(){
		Session session = Session.getActiveSession();	
		
        List<String> permissions = session.getPermissions();
        if (!permissions.contains(CONSTANTS.PUB_CREATE_EVENT)) {                     
        	requestPublishPermissions(session);
            return;
        }
        
		new Request(
			    session,
			    "/" + eventId,
			    null,
			    HttpMethod.DELETE,
			    new Request.Callback() {
			        public void onCompleted(Response response) {
			            /* handle the result */		         
	                	JSONObject object = response.getGraphObject().getInnerJSONObject();	                	
						try {
							boolean result = object.getBoolean("FACEBOOK_NON_JSON_RESULT");
							if(result){
								Toast.makeText(getActivity(), "Event canceled.", Toast.LENGTH_SHORT).show();
								switchView(EventPagerFragment.newInstance(0), FRAGMENT_NAME.EVENT, true, null);													
							}
							else{
								handleError(response.getError());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	                
			        }
			    }
		).executeAsync();
	}
	
	/**
	 * Update event
	 */
	private void updateEvent(){
		Session session = Session.getActiveSession();	
		
        List<String> permissions = session.getPermissions();
        if (!permissions.contains(CONSTANTS.PUB_CREATE_EVENT)) {                     
        	requestPublishPermissions(session);
            return;
        }
        
        String eventStartTime = new DateHelper().toFBDateFormat(edDate.getText().toString(), edTime.getText().toString());
		Bundle params = new Bundle();
		params.putString("name", CONSTANTS.EVENT_PREFIX + edEventName.getText().toString().trim());		
		params.putString("description", edDetails.getText().toString().trim());
		String location = "";
//		if(edLocation.getText().toString().trim().equals(null))
//			location = edLocation.getText().toString().trim();
//		params.putString("location", location);
		if(!place_id.equals("")){
			params.putString("location_id", place_id);
			location = edLocation.getText().toString().trim();
		}
		params.putString("start_time", eventStartTime);
				
		/* make the API call */
		new Request(
		    session,
		    "/" + eventId,
		    params,
		    HttpMethod.POST,
		    new Request.Callback() {
		        public void onCompleted(Response response) {
		            /* handle the result */
		        	if (response.getError() != null) {
	                    handleError(response.getError());
	                }else{              		                	
	                	JSONObject object = response.getGraphObject().getInnerJSONObject();					
						try {
							boolean result = object.getBoolean("FACEBOOK_NON_JSON_RESULT");
							if(result){
								//Retrieve created event and send to DetailsFragment
								getUpdatedEvent(eventId);
							}else{
								Toast.makeText(getActivity(), "Update failed", Toast.LENGTH_SHORT).show();
							}
							
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
	 * Get updated event and show
	 */
	public void getUpdatedEvent(String eventId) {
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
						        	
						        	Toast.makeText(getActivity(), "Event was updated.", Toast.LENGTH_SHORT).show();
						        							        	
						        	// Move to Details Fragment						        
						        	switchToDetailView(FRAGMENT_NAME.EDIT, true, event);	
						        	
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
	
	/**
	 * Validate data before update	 
	 */
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
		
	/**
	 * Request create_event permission if need
	 * @param session
	 */
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
		          Log.e(TAG, "Unable to deserialize place object.", e);
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
