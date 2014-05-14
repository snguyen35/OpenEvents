package csc.mobility.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class Event implements Parcelable {
	
	private String id;
	private String name;
	private String ownerId;	
	private String description;
	private String start_time;
	private String location;
	private String status;
	private Venue venue;
	private String picSmall;
	
	/** 
	 * Standard basic constructor for non-parcel object creation 
	 */	
	public Event(){
		
	}
	
	public Event(String id, String name, String ownerId, String description,
			String start_time, String location, Venue venue) {
		super();
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.description = description;
		this.start_time = start_time;
		this.location = location;
		this.venue = venue;
	}
	
	public Event(String id, String name, String ownerId, String description,
			String start_time, String location, Venue venue, String picSmall){
		super();
		this.id = id;
		this.name = name;
		this.ownerId = ownerId;
		this.description = description;
		this.start_time = start_time;
		this.location = location;
		this.venue = venue;
		this.picSmall=picSmall;
	}
	
	public String getpicSmall(){
		return picSmall;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setPicSmall(String pic){
		this.picSmall=pic;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	/*
	 * Overwrite methods of Parcelable Interface    
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		// Need to write each field into the parcel. 		
		Log.v("Event object : ", "Write To Parcel..."+ flags);
		out.writeString(id); 
		out.writeString(name); 
		out.writeString(ownerId); 
		out.writeString(description); 
		out.writeString(start_time); 
		out.writeString(location); 
		out.writeString(status); 
		out.writeParcelable(venue, flags); 
		
	}
	
	private void readFromParcel(Parcel in) {   
		// Read back each field in the order that it was written to the parcel 
		Log.v("Event object : ", "Time to put back parcel data...");
		id = in.readString(); 
		name = in.readString();		
		ownerId = in.readString(); 		
		description = in.readString(); 
		start_time = in.readString(); 
		location = in.readString(); 
		status = in.readString(); 
		venue = in.readParcelable(getClass().getClassLoader()); 		
	}
	
	public static final Parcelable.Creator<Event> CREATOR
				= new Parcelable.Creator<Event>() {
		
		public Event createFromParcel(Parcel in) {
			return new Event(in);
		}
	
		public Event[] newArray(int size) {
			return new Event[size];
		}
	};
	
	/**
	* Constructor to use when re-constructing object from a parcel 
	* @param in a parcel from which to read this object 
	*/	
	public Event(Parcel in) { 
		readFromParcel(in); 
	}	
	
}

