package csc.mobility.openevents.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import csc.mobility.constant.CONSTANTS;
import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.Event;
//import csc.mobility.entity.Constants;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.adapter.EventListAdapter;
public class EventFragmentfromMap extends BaseFragment implements OnItemClickListener {

	private View rootView;
	private TextView tvNoResult;
	private ListView eventList;
	private EventListAdapter eventAdapter;
	private ArrayList<Event> items = new ArrayList<Event>();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		setHasOptionsMenu(true);
		
	
		//((MainActivity) getActivity()).getActionBar().setTitle("Events");
		Bundle args = getArguments();
		items = args.getParcelableArrayList(CONSTANTS.KEY_EVENT_LIST);		
		
		rootView = inflater.inflate(R.layout.fragment_event_from_map, container, false);
		tvNoResult=(TextView) rootView.findViewById(R.id.tvNoResult);
		eventList = (ListView) rootView.findViewById(R.id.eventListView);			
		eventList.setOnItemClickListener(this);	
		
		if(items.size()==0){
		tvNoResult.setText("No event found");
		tvNoResult.setVisibility(View.VISIBLE);
		eventList.setVisibility(View.GONE);
		}
		else{
						
		eventAdapter = new EventListAdapter(getActivity(), items);
		eventList.setAdapter(eventAdapter); 		
		eventList.setVisibility(View.VISIBLE);
		eventList.setAdapter(eventAdapter); 	
		tvNoResult.setVisibility(View.GONE);
		}
		return rootView;
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {		
		Event item = (Event) eventList.getItemAtPosition(position);		
		switchToDetailView(FRAGMENT_NAME.SEARCH,true,item);		
	}
	
	
	
}