package csc.mobility.service;


import java.util.ArrayList;
import org.json.JSONException;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import csc.mobility.entity.Event;
import csc.mobility.entity.EventAdapter;
import csc.mobility.helper.ParseJSON;

public class eventService {
	private ArrayList <Event> eventList= new ArrayList <Event>();
	private EventAdapter eventAdapter=new EventAdapter();
		
	
	public Event getEvent(String eventId){
		Event event = null;
		String fqlQuery = "SELECT eid, name, creator, description, start_time, location, venue"
				+ " FROM event"
				+ " WHERE eid = " + eventId;
		
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
			   } catch (JSONException e) {								
					e.printStackTrace();
			   }                
			}                  
		}); 
		request.executeAsync();
	
		return event;
	}
	
	public ArrayList<Event> getEventList(){
		String fqlQuery = "SELECT eid, name, creator, description, pic_small, pic_big, venue, location, start_time from event " +
		 		"WHERE eid in (SELECT eid FROM event_member WHERE uid = me()) and location <>'' and strlen(venue.longitude)<>0 and strlen(venue.latitude)<>0 and strlen(start_time)<>0 and name<>'' and description<>''";
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
							
						} catch (JSONException e) {								
							e.printStackTrace();
						}              
       
               }                  
       }); 
       Request.executeBatchAsync(request);
	return eventList;            		
   }

	
	 




}
