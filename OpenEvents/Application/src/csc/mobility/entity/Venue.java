package csc.mobility.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Venue implements Parcelable {
	
	private String latitude;
	private String longitude;	
    
	/** 
	 * Standard basic constructor for non-parcel object creation 
	 */	
	public Venue() {		
	}
		
	public Venue(String lat, String lng){
		this.latitude= lat;
		this.longitude= lng;
	}
		
	public String getLatitude() {
		return latitude;
	}

	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}


	public String getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		this.longitude = longitude;
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
		Log.v("Venue object : ", "Write To Parcel..."+ flags);
		out.writeString(latitude); 
		out.writeString(longitude); 
	}	
	
	private void readFromParcel(Parcel in) {   
		// Read back each field in the order that it was written to the parcel 
		Log.v("Venue object : ", "Time to put back parcel data...");
		latitude = in.readString(); 
		longitude = in.readString();		
	}
		
	public static final Parcelable.Creator<Venue> CREATOR
				= new Parcelable.Creator<Venue>() {
		
		public Venue createFromParcel(Parcel in) {
		    return new Venue(in);
		}
	
		public Venue[] newArray(int size) {
		    return new Venue[size];
		}
	};
	
	/**
	 * Constructor to use when re-constructing object from a parcel 
	 * @param in a parcel from which to read this object 
	 */	
	public Venue(Parcel in) { 
		readFromParcel(in); 
	}
}
