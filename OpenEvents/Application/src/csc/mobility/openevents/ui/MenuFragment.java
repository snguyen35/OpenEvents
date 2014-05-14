package csc.mobility.openevents.ui;

import java.util.ArrayList;

import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.SlidingMenuItem;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.adapter.SlidingMenuAdater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MenuFragment extends ListFragment {
		
	private View rootView;
	private TypedArray item_icons;
	private String[] item_names;
	public FBLogOutListener mListener;
	
	public interface FBLogOutListener{
		void doLogout();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//return inflater.inflate(R.layout.menu_fragment,  null);
		rootView = inflater.inflate(R.layout.menu_fragment, container, false);
		return rootView;
	}

	@SuppressLint("Recycle")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		item_icons = getResources().obtainTypedArray(R.array.menu_item_icons);
		item_names = getResources().getStringArray(R.array.menu_item_names);
		
		ArrayList<SlidingMenuItem> menus = new ArrayList<SlidingMenuItem>();		
			menus.add(new SlidingMenuItem(9000, item_icons.getResourceId(0, -1), item_names[0]));
			menus.add(new SlidingMenuItem(9001, item_icons.getResourceId(1, -1), item_names[1]));
			menus.add(new SlidingMenuItem(9002, item_icons.getResourceId(2, -1), item_names[2]));
			menus.add(new SlidingMenuItem(9003, item_icons.getResourceId(3, -1), item_names[3], "5", true));
			menus.add(new SlidingMenuItem(9004, item_icons.getResourceId(4, -1), item_names[4]));
			menus.add(new SlidingMenuItem(9005, item_icons.getResourceId(5, -1), item_names[5]));				
			
		setListAdapter(new SlidingMenuAdater(getActivity(), menus));		
	}
	
	public void onListItemClick(ListView lv, View v, int position, long id) {
		
		SlidingMenuItem item = (SlidingMenuItem)getListAdapter().getItem(position);		
		Fragment newContent = null;
		CharSequence actionBarTitle = "";
		
		switch (item.getId()) {
		case 9000:
			newContent = new SearchFragment();					
			actionBarTitle = FRAGMENT_NAME.SEARCH;
			break;
		case 9001:
			newContent = EventPagerFragment.newInstance(0);	
			actionBarTitle = FRAGMENT_NAME.EVENT;
			break;	
		case 9002:
			newContent = new CreateFragment();
			actionBarTitle = FRAGMENT_NAME.CREATE;
			break;		
		case 9003:
			newContent = new NotificationFragment();
			actionBarTitle = FRAGMENT_NAME.NOTIFICATION;
			break;
		case 9004:			
	        logoutFB();
			break;
		case 9005:
			newContent = new HelpFragment();
			actionBarTitle = FRAGMENT_NAME.HELP;
			break;
		}		
		if(newContent != null)
			switchMenu(newContent, actionBarTitle);
	}
		
	private void switchMenu(Fragment fragment, CharSequence title) {
		if (getActivity() == null)
			return;
		
		MainActivity main = (MainActivity) getActivity();		
		main.switchMenu(fragment, title, true);		
	}	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		if(activity instanceof FBLogOutListener){
			mListener = (FBLogOutListener) activity;
		}
		else{
			throw new ClassCastException(activity.toString() + "MUST implement FBLogOutListener");
		}
	}

	private void logoutFB(){
		DialogInterface.OnClickListener dlogClickListener = new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (which) {
					// Yes button clicked
					case DialogInterface.BUTTON_POSITIVE:
							mListener.doLogout();
						break;
	
					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked 
						// Do nothing & return
						break;
				}
			}
		};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Logout");
		builder.setMessage("Are you sure you want to logout ?")
				.setPositiveButton("Yes", dlogClickListener)
				.setNegativeButton("No", dlogClickListener).show();
	}

}
