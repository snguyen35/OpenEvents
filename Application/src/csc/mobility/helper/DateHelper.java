package csc.mobility.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
	
	public String toFBDateFormat(Date date){	
		SimpleDateFormat fbFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
		return fbFormat.format(date).toString();
	}
	
	public String toFBDateFormat(String dateStr, String timeStr){		
		Date date = new Date();       
		if(!dateStr.contains("/")){	        	
        	dateStr = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(date).toString();
        }
        if(!timeStr.contains(":")){
        	timeStr = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date).toString();
        }
        	        
        String datetimeStr = dateStr + " " + timeStr;	   
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());     
        	
        Date selectedDate = null;
        try {
			selectedDate = sdf.parse(datetimeStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        SimpleDateFormat fbFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        if(selectedDate != null)
        	return fbFormat.format(selectedDate).toString();
        else
        	return fbFormat.format(date).toString();		
	}
	
	public String fromFBDateFormat(String fbDateStr){
		String result = "";
		SimpleDateFormat fbFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
		SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM/dd/yyyy/hh:mm aaa", Locale.getDefault());		
		if(!fbDateStr.contains(":")){
			fbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		}
		try {						
			Date testDate = fbFormat.parse(fbDateStr);			
			result = outputFormat.format(testDate);			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return result;
	}
	
	public String to24HoursFormat(String dateStr){
		String result = "";
		SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM/dd/yyyy/hh:mm aaa", Locale.getDefault());
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy-HH:mm", Locale.getDefault());
		
		try {
			Date date = inputFormat.parse(dateStr);
			result = outputFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
}
