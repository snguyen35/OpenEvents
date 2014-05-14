package csc.mobility.openevents.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import csc.mobility.helper.DateHelper;
import csc.mobility.openevents.R;

public class CustomCalendarView extends LinearLayout{
	
	private TextView tvMonth, tvDay, tvTime;		
	
	public CustomCalendarView(Context context) {
		super(context, null);
		// TODO Auto-generated constructor stub
	}	
	
	public CustomCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		final TypedArray a = context.obtainStyledAttributes(attrs,
		        R.styleable.CustomCalendarView, 0, 0);    
						
		int headerBGColor = a.getColor(R.styleable.CustomCalendarView_headerBGColor, Color.WHITE);
		int bodyBGColor = a.getColor(R.styleable.CustomCalendarView_bodyBGColor, Color.WHITE);		
	    int headerTextColor = a.getColor(R.styleable.CustomCalendarView_headerTextColor, Color.BLACK);
	    int bodyTextColor = a.getColor(R.styleable.CustomCalendarView_bodyTextColor, Color.BLACK);	    
	  
	    a.recycle();
	    
	    // Style main layout
	    setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_VERTICAL);
		setWeightSum((float) 10);
		setBackgroundColor(bodyBGColor);
	    
	    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    inflater.inflate(R.layout.view_custom_calendar, this, true);
				 
	    tvMonth = (TextView) getChildAt(0);	  
	    tvMonth.setTextSize(11.0f);
	    tvMonth.setTextColor(headerTextColor);
	    tvMonth.setBackgroundColor(headerBGColor);
	    
	    tvDay = (TextView) getChildAt(1);	
	    tvDay.setTextSize(11.0f);
	    tvDay.setTextColor(bodyTextColor);	    
	    
	    tvTime = (TextView) getChildAt(2);	   
	    tvTime.setTextSize(11.0f);
	    tvTime.setTextColor(bodyTextColor);		    
	}


	public void setDate(String date) {
		//String[] result = new DateHelper().fromFBDateFormat(date).split("\\/");
		String[] result = date.split("\\/");
		this.tvMonth.setText(result[0]);
		this.tvDay.setText(result[1]);
		this.tvTime.setText(result[3]);	
	}
	
}
