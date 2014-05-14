package csc.mobility.helper;


import com.google.android.gms.maps.model.LatLng;

public class findOffSetFromMetter {
	
	public static LatLng convert(double latitude, double longitude, int distance){
		
		 //Earth’s radius, sphere
		 /*double R=6378137;

		 //offsets in meters
		
		 //Coordinate offsets in radians
		 double dLat = distance/R;
		 double dLon = (distance/R)/Math.cos(Math.PI*latitude/180);

		 //OffsetPosition, decimal degrees
		 double latO =  dLat * 180/Math.PI;
		 double lonO =  dLon * 180/Math.PI;
		 
		//double latO =  (180/Math.PI)*(distance/6378137);
		//double lonO =  (180/Math.PI)*(distance/6378137)/Math.cos(Math.PI*latitude/180);*/
		 
		
        final double latRadian = Math.toRadians(latitude);

        final double degLatKm = 110.574235;
        final double degLongKm = 110.572833 * Math.cos(latRadian);
        final double deltaLat = distance / 1000.0 / degLatKm;
        final double deltaLong = distance / 1000.0 / degLongKm;

        		 
		LatLng latlong=new LatLng(deltaLat,deltaLong);		
		 
		return latlong;
		
	}

}
