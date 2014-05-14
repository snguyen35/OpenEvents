package csc.mobility.openevents.ui.adapter;

import java.util.ArrayList;

import csc.mobility.entity.Attendess;
import csc.mobility.helper.imagedownloader.ImageLoader;
import csc.mobility.openevents.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AttendeesListAdapter extends BaseAdapter{

	private Context ctx;
	private ArrayList<Attendess> data;
	private ImageLoader imageLoader; 
	
	
	
	public AttendeesListAdapter(Context ctx, ArrayList<Attendess> data) {
		super();
		this.ctx = ctx;
		this.data = data;
		imageLoader = new ImageLoader(ctx);
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
		AttendeesViewHolder holder;
		// Only inflate a new View when the convertView is null.
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.attendees_item, null);
			
			// Create a ViewHolder and store references to the children views
			holder = new AttendeesViewHolder();
			holder.imgProfile = (ImageView) convertView.findViewById(R.id.attendees_profile_image);
			holder.tvName = (TextView) convertView.findViewById(R.id.attendees_name);					
			
			// Store the holder with the view.
			convertView.setTag(holder);
			
		}else{
			// convertView is not null, reuse it directly, no need to inflate
			// Get the ViewHolder back to get fast access to the views
			holder = (AttendeesViewHolder) convertView.getTag();
		}
		
		Attendess item = data.get(position);				
		// Bind data		
		holder.tvName.setText(item.getName());
		imageLoader.DisplayImage(item.getImgURL(), holder.imgProfile);
					
		return convertView;
	}
	
	// Implement the View Holder pattern
	private static class AttendeesViewHolder{
		protected ImageView imgProfile;
		protected TextView tvName;		
	}

}
