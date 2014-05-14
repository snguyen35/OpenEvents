package csc.mobility.entity;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import csc.mobility.helper.DateHelper;


public class EventAdapter {
ArrayList <Event> eventList=new ArrayList<Event>() ;
JSONArray array;
public EventAdapter(JSONArray array){
	this.array=array;	
}
public EventAdapter() {
	
}

public void setEvenList(JSONArray array){
	this.array=array;
}

public ArrayList<Event> getEvenList() throws JSONException{
	String eID="";
	String name="";	 
	String description="";	 
	String location="";
	JSONObject venueJSON;	
	String strJSONVenue="";
	Venue venue;	
	String ownerId="";
	String strDatetime="";	
	String picSmall="";
	 
	//	JSONObjectDecode jsonArray = (JSONObjectDecode) array;		
	//	for (int i = 0; i < jsonArray.length(); i++) {
	//		eventList.add(jsonArray.getEvent(i));
	//	}
	 
	for (int i = 0; i < array.length(); i++) { 
		 
		JSONObject object = (JSONObject) array.get(i);     
		if (!object.isNull("eid"))  eID=object.getString("eid");
		if (!object.isNull("name")) {
			name=object.getString("name");
			name=name.replace("[CSCVN]", "");//remove prefix csc
		}
		if (!object.isNull("description")) description=object.getString("description");
		if (!object.isNull("start_time")) strDatetime=object.getString("start_time").toString();		
		strDatetime = new DateHelper().fromFBDateFormat(strDatetime);
		if (!object.isNull("creator")) ownerId = object.getString("creator");
		if (!object.isNull("location")) location=object.getString("location");
		if(!object.isNull("pic_small")) picSmall=object.getString("pic_small");
		
		venue = new Venue();
		if (!object.isNull("venue")) {
			strJSONVenue = object.getString("venue");
			 venueJSON = new JSONObject(strJSONVenue);
			
			if (!venueJSON.isNull("latitude"))
				venue.setLatitude(venueJSON.get("latitude").toString());

			if (!venueJSON.isNull("longitude"))
				venue.setLongitude(venueJSON.get("longitude").toString());	
		}
				   
		Event event=new Event(eID, name, ownerId, description, strDatetime,location, venue, picSmall);
		eventList.add(event);
	}
	return eventList;	
}
}
