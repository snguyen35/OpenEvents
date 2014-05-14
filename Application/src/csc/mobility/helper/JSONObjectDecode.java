package csc.mobility.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import csc.mobility.entity.Attendess;
import csc.mobility.entity.Event;
import csc.mobility.entity.EventStatus;
import csc.mobility.entity.Venue;

public class JSONObjectDecode extends JSONArray{
	
	public JSONObjectDecode(String json) throws JSONException {
		super(json);
	}
			
	public Event getEvent(int index) throws JSONException {		
		JSONObject object;
		object = getJSONObject(index);

		String id = "";
		if (!object.isNull("eid"))
			id = object.getString("eid");

		String ownerId = "";		
		if (!object.isNull("creator"))
			ownerId = object.getString("creator");
			
		String name = "";
		if (!object.isNull("name"))
			name = object.getString("name").toString();
			
		String description = "";
		if (!object.isNull("description"))
			description = object.getString("description").toString();

		String start_time = "";
		if (!object.isNull("start_time"))
			start_time = object.getString("start_time").toString();
			start_time = new DateHelper().fromFBDateFormat(start_time);
		
		String location = "";
		if (!object.isNull("location"))
			location = object.getString("location").toString();
		
		String jsonVenue = "";
		Venue venue = new Venue();
		if (!object.isNull("venue")) {
			jsonVenue = object.getString("venue");
			JSONObject venueJSON = new JSONObject(jsonVenue);
			
			if (!venueJSON.isNull("latitude"))
				venue.setLatitude(venueJSON.get("latitude").toString());

			if (!venueJSON.isNull("longitude"))
				venue.setLongitude(venueJSON.get("longitude").toString());			
		}
		
		return new Event(id, name, ownerId, description, start_time, location, venue);
	}
	
	public EventStatus getEventStatus(int index) throws JSONException{
		JSONObject object;
		object = getJSONObject(index);
		
		String eid = "";
		if (!object.isNull("eid"))
			eid = object.getString("eid").toString();
		
		String rsvp_status = "";
		if (!object.isNull("rsvp_status"))
			rsvp_status = object.getString("rsvp_status").toString();
		
		return new EventStatus(eid, rsvp_status);
	}
	
	public Attendess getAttendess(int index) throws JSONException{
		JSONObject object;
		object = getJSONObject(index);

		String name = "";
		if (!object.isNull("name"))
			name = object.getString("name");
			
		String pic_square = "";
		if (!object.isNull("pic_square"))
			pic_square = object.getString("pic_square").toString();
		
		String profile_url = "";		
		if (!object.isNull("profile_url"))
			profile_url = object.getString("profile_url");
		
		return new Attendess(name, pic_square, profile_url);
	}
	
}
