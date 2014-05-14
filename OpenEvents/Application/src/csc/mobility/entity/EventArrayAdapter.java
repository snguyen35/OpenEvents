/*package csc.mobility.entity;

import java.util.ArrayList;
import java.util.Date;

import csc.mobility.openevents.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventArrayAdapter extends ArrayAdapter<Event> {
	private final Activity context; 	
	private final ArrayList<Event> events; 
	private int resourceId;
	public EventArrayAdapter(
	        Activity context, 
	        int resourceId, 
	        ArrayList<Event> events) {
	    super(context, resourceId, events);
	    this.context = context;
	    this.events = events;
	    this.resourceId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		EventViewHolder viewholder;
		if (rowView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = vi.inflate(resourceId, null);
			viewholder=new EventViewHolder();
			viewholder.Name=(TextView) rowView.findViewById(R.id.eventName);
			viewholder.Location=(TextView) rowView.findViewById(R.id.eventLocation);
			viewholder.Month=(TextView) rowView.findViewById(R.id.eventMonth);
			viewholder.Day=(TextView) rowView.findViewById(R.id.eventDay);
			viewholder.Time=(TextView) rowView.findViewById(R.id.eventTime);
			rowView.setTag(viewholder);
		}
		else{
		viewholder=(EventViewHolder)rowView.getTag();	
		}
		Event event = (Event) events.get(position);
		DateTime eventDate=event.getDate();
		viewholder.Name.setText(event.getName());
		viewholder.Location.setText(event.getLocation());
		viewholder.Day.setText(eventDate.getDay());
		viewholder.Month.setText(eventDate.getMonth());
		viewholder.Time.setText(eventDate.getTime());
		return rowView;
	}
}*/
