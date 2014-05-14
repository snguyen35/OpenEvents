package csc.mobility.openevents.ui;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookRequestError.Category;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import csc.mobility.constant.CONSTANTS;
import csc.mobility.constant.EVENT_STATUS;
import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.Event;
import csc.mobility.helper.JSONObjectDecode;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.adapter.EventListAdapter;

public class EventFragment extends BaseFragment implements OnItemClickListener {
	
	// private static final String TAG = "EventFragment";
	private View rootView;
	private int mIndex;
	private ListView eventList;
	private EventListAdapter eventAdapter;
	private TextView tvNoResult;
	private LinearLayout progressBar;
				
	public static EventFragment newInstance(int index){
		EventFragment ef = new EventFragment();
		//ef.mIndex = index;
		Bundle args = new Bundle();
        args.putInt("frg_index", index);
        ef.setArguments(args);
		return ef;
	}
		   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIndex = getArguments().getInt("frg_index");				
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub						
		rootView = inflater.inflate(R.layout.fragment_event, container, false);					
				
		progressBar = (LinearLayout) rootView.findViewById(R.id.progressBar);
		tvNoResult = (TextView) rootView.findViewById(R.id.tvNoResult);
		eventList = (ListView) rootView.findViewById(R.id.listEvent);
		eventList.setOnItemClickListener(this);
		
		updateView();			
		return rootView;
	}
	    
	@Override
	public void updateView() {
		// TODO Auto-generated method stub		
		progressBar.setVisibility(View.VISIBLE);
		
		eventList.setVisibility(View.GONE);
		tvNoResult.setVisibility(View.GONE);
		
		Session session = Session.getActiveSession();
        if (session.getState() == SessionState.OPENED) {
        	String myEventQuery = "SELECT eid, name, creator, description, start_time, location, venue " +
        			"FROM event WHERE creator = me()";
        	String q1 = "SELECT eid FROM event_member WHERE uid=me() AND rsvp_status='";     
        	String q2 = "SELECT eid, name, creator, description, start_time, location, venue FROM event" + CONSTANTS.SPACE;
    		        	
        	switch(mIndex){
            case 0:
            	// Created - My Event
            	// myEventQuery
            	break;
            case 1:
            	// Attending - Going
            	q1 += EVENT_STATUS.ATTENDING + "'";
            	q2 += "WHERE eid IN (SELECT eid FROM #q1) AND creator <> me()";
            	break;
            case 2:
            	// Unsure - Maybe 
            	q1 += EVENT_STATUS.FQL_UNSURE + "'";      
            	q2 += "WHERE eid IN (SELECT eid FROM #q1)";
            	break;
            case 3:
            	// Declined - Not Going 
            	q1 += EVENT_STATUS.DECLINED + "'";    
            	q2 += "WHERE eid IN (SELECT eid FROM #q1)";
            	break;
            case 4:            	
            	// Not reply - Be Invited 
            	q1 += EVENT_STATUS.NOT_REPLIED + "'";    
            	q2 += "WHERE eid IN (SELECT eid FROM #q1)";
            	break;
    		}
    		
        	myEventQuery += "AND (start_time > now() or end_time > now()) " +
    			  "AND strlen(venue.longitude)<>0 and strlen(venue.latitude)<>0 " +
        		  "ORDER BY start_time DESC limit 0,500";
        	
    		q2 += "AND (start_time > now() or end_time > now()) " +
    			  "AND strlen(venue.longitude)<>0 and strlen(venue.latitude)<>0 " +
        		  "ORDER BY start_time DESC limit 0,500";
    		
    		JSONObject jsonFQL = new JSONObject();
    		try {
				jsonFQL.put("q1", q1);
				jsonFQL.put("q2", q2);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
    		
    		String graphPath = "/fql";   
    		Bundle params = new Bundle();
    		if(mIndex == 0){
    			params.putString("q", myEventQuery);
    		}else{
    			params.putString("q", jsonFQL.toString());
    		}
    		
    		Request request = new Request(session, graphPath, params, HttpMethod.GET, new Request.Callback() {				
				@Override
				public void onCompleted(Response response) {
					// TODO Auto-generated method stub
					if (response.getError() != null) {
	                    handleError(response.getError());
	                }else{                							
						try{
							JSONObject jsonObject = response.getGraphObject().getInnerJSONObject();	
							String jsonData = "";
							if(mIndex == 0){
								if(!jsonObject.isNull("data"))
						           jsonData = jsonObject.getString("data").toString();
							}else{								
								JSONObject jsonEventObj = jsonObject.getJSONArray("data").getJSONObject(1);
								
								if(!jsonEventObj.isNull("fql_result_set")){
									jsonData = jsonEventObj.getString("fql_result_set").toString();
								}																		
							}														
													
							JSONObjectDecode eventArray = new JSONObjectDecode(jsonData);	
							ArrayList<Event> items = new ArrayList<Event>();
							for (int i = 0; i < eventArray.length(); i++) {		
								
								Event e = eventArray.getEvent(i);	
								switch (mIndex) {
								case 0:
									e.setStatus(EVENT_STATUS.ATTENDING);
									break;
								case 1:
									e.setStatus(EVENT_STATUS.ATTENDING);								
									break;
								case 2:
									e.setStatus(EVENT_STATUS.FQL_UNSURE);
									break;
								case 3:
									e.setStatus(EVENT_STATUS.DECLINED);
									break;
								case 4:
									e.setStatus(EVENT_STATUS.NOT_REPLIED);									
									break;
								}
							
								items.add(e);
				           	}							
							
							if(items.size() == 0){								
								tvNoResult.setText("No event found");
								tvNoResult.setVisibility(View.VISIBLE);
								eventList.setVisibility(View.GONE);
					    		progressBar.setVisibility(View.GONE);					    		
							}else{										
								eventAdapter = new EventListAdapter(getActivity(), items);
					    		eventList.setAdapter(eventAdapter); 		
					    		eventList.setVisibility(View.VISIBLE);
					    		progressBar.setVisibility(View.GONE);
					    		tvNoResult.setVisibility(View.GONE);
							}				           	
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});    		
    		RequestAsyncTask task = new RequestAsyncTask(request);
    		task.execute();	   		    		   		
        }else{
        	startActivity(new Intent(getActivity(), LoginActivity.class));        	
        }		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Event item = (Event) eventList.getItemAtPosition(position);		
		switchView(new DetailsPagerFragment(), FRAGMENT_NAME.DETAILS, true, item);
		//switchToDetailView(FRAGMENT_NAME.EVENT, true, item);				
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
                    //requestReadPermissions(Session.getActiveSession());
                }
            };
            
            new AlertDialog.Builder(getActivity())
            .setPositiveButton(R.string.error_dialog_button_text, listener)
            .setTitle(R.string.error_dialog_title)
            .setMessage(dialogBody)
            .show();
		}		
	}	
	
}