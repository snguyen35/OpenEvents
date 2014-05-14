package csc.mobility.openevents.ui;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import csc.mobility.entity.Attendess;
import csc.mobility.helper.JSONObjectDecode;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.adapter.AttendeesListAdapter;

public class AttendeeFragment extends BaseFragment implements OnItemClickListener{
	private View rootView;	
	private String eventId="";
	private ListView attendeesList;
	private AttendeesListAdapter attendeesAdapter;
	private TextView tvNoResult;
	
	public static AttendeeFragment newInstance(String eventId){
		Bundle args = new Bundle();		
		args.putString("eventId", eventId);
		AttendeeFragment af = new AttendeeFragment();
        af.setArguments(args);
		return af;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args!=null) {					
			eventId= args.getString("eventId");			
		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub						
		rootView = inflater.inflate(R.layout.fragment_attendees, container, false);					
				
		tvNoResult = (TextView) rootView.findViewById(R.id.tvNoResult);
		attendeesList = (ListView) rootView.findViewById(R.id.listAttendees);
		attendeesList.setOnItemClickListener(this);	
		
		updateView();	
		return rootView;
	}

	

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		super.updateView();
		Session session = Session.getActiveSession();
        if (session.getState() == SessionState.OPENED) {
        	String fqlQuery = "SELECT name, profile_url, pic_square FROM user " +
        			"WHERE uid IN (SELECT uid FROM event_member WHERE eid="+ eventId + " AND rsvp_status='attending')";
        	
        	String graphPath = "/fql";   
    		Bundle params = new Bundle();
    		params.putString("q", fqlQuery);
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
							if(!jsonObject.isNull("data"))
						           jsonData = jsonObject.getString("data").toString();
							
							JSONObjectDecode attendeesArray = new JSONObjectDecode(jsonData);	
							ArrayList<Attendess> items = new ArrayList<Attendess>();
							for (int i = 0; i < attendeesArray.length(); i++) {	
								items.add(attendeesArray.getAttendess(i));
							}
							if(items.size() == 0){											
								tvNoResult.setVisibility(View.VISIBLE);
								attendeesList.setVisibility(View.GONE);					    						    		
							}else{										
								attendeesAdapter = new AttendeesListAdapter(getActivity(), items);
								attendeesList.setAdapter(attendeesAdapter); 		
								attendeesList.setVisibility(View.VISIBLE);					    		
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
		// TODO Auto-generated method stub
		Attendess item = (Attendess) attendeesList.getItemAtPosition(position);	
		
		Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(item.getProfileURL()));
        startActivity(i);		
	}
	
}
