package csc.mobility.openevents.ui.adapter;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import csc.mobility.constant.CONSTANTS;
import csc.mobility.entity.Event;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.customview.CustomCalendarView;

public class EventListAdapter extends BaseAdapter {

	private Context ctx;
	private ArrayList<Event> data;
	
	
	public EventListAdapter(Context ctx, ArrayList<Event> list) {
		super();
		this.ctx = ctx;
		this.data = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		EventViewHolder holder;
		// Only inflate a new View when the convertView is null.
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.event_item_row, null);
			
			// Create a ViewHolder and store references to the children views
			holder = new EventViewHolder();
			holder.ccv = (CustomCalendarView) convertView.findViewById(R.id.calendarView);
			holder.tvEventTitle = (TextView) convertView.findViewById(R.id.tvEventTitle);		
			holder.tvEventLocation = (TextView) convertView.findViewById(R.id.tvEventLocation);		
			
			// Store the holder with the view.
			convertView.setTag(holder);
			
		}else{
			// convertView is not null, reuse it directly, no need to inflate
			// Get the ViewHolder back to get fast access to the views
			holder = (EventViewHolder) convertView.getTag();
		}
		
		final Event item = data.get(position);		
		
		// Bind that data
		String eventName = item.getName();
		if(!eventName.equals(""))
			eventName = eventName.replace(CONSTANTS.EVENT_PREFIX, "");
		
		holder.ccv.setDate(item.getStart_time());
		holder.tvEventTitle.setText(eventName);
		holder.tvEventLocation.setText("Location: " + item.getLocation());				
				
		return convertView;
	}


	
	// Implement the View Holder pattern
	private static class EventViewHolder{
		protected CustomCalendarView ccv;
		protected TextView tvEventTitle, tvEventLocation;		
	}
	

}


