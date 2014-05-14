package csc.mobility.helper;

import org.json.JSONException;
import org.json.JSONObject;

import csc.mobility.entity.Event;
import csc.mobility.entity.Venue;

public class JSONSingleObjectDecode {

	public JSONSingleObjectDecode() {
		super();
	}
	
	public Event getEvent(JSONObject object) throws JSONException {
		
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
	
	
}
