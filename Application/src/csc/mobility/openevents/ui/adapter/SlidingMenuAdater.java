package csc.mobility.openevents.ui.adapter;

import java.util.ArrayList;

import csc.mobility.entity.SlidingMenuItem;
import csc.mobility.openevents.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingMenuAdater extends BaseAdapter {

	private Context ctx;
	private ArrayList<SlidingMenuItem> data;
	
	
	public SlidingMenuAdater(Context ctx, ArrayList<SlidingMenuItem> list) {
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
		MenuHolder holder;
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.menu_row, null);
			
			holder = new MenuHolder();
			holder.imgIcon = (ImageView) convertView.findViewById(R.id.menu_item_icon);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.menu_item_name);
			holder.txtCount = (TextView) convertView.findViewById(R.id.counter);
			
			convertView.setTag(holder);
		}else{
			holder = (MenuHolder) convertView.getTag();
		}
		
		
		
		holder.imgIcon.setImageResource(data.get(position).getIcon());
		holder.txtTitle.setText(data.get(position).getName());
		
		if (data.get(position).isCounterVisible()) {
			holder.txtCount.setText(data.get(position).getNum());
		} else {
			holder.txtCount.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	private static class MenuHolder{
		ImageView imgIcon;
		TextView txtTitle, txtCount;		
	}
}
