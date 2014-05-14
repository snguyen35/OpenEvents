package csc.mobility.openevents.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.adapter.EventPagerAdapter;

public class EventPagerFragment extends Fragment {

	View rootView;
	private int mIndex; // index of child views inside view pager
	private ViewPager mViewPager;
    private EventPagerAdapter mPagerAdapter;      
    TextView tvSearch;
        
    public static EventPagerFragment newInstance(int index){
    	EventPagerFragment fragment = new EventPagerFragment();
		//ef.mIndex = index;
		Bundle args = new Bundle();
        args.putInt("frg_index", index);
        fragment.setArguments(args);
		return fragment;
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		if(getArguments() == null){
			mIndex = 0;	
		}else{
			mIndex = getArguments().getInt("frg_index");	
		}    
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub				
		rootView = inflater.inflate(R.layout.fragment_event_pager, container, false);
						
		// Initilization				
		mViewPager=(ViewPager)rootView.findViewById(R.id.pager);
		mViewPager.setAdapter(buildAdapter());
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {					
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
//				tabStrip.getChildAt(position).setBackgroundColor(Color.BLUE);
				switch (position) {
				case 0:
					((MainActivity) getActivity()).set_TOUCHMODE_FULLSCREEN();
					break;
				default:
					((MainActivity) getActivity()).set_TOUCHMODE_MARGIN();
					break;
				}
			}
		});	
		mViewPager.setCurrentItem(mIndex);
		
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub		
	    inflater.inflate(R.menu.event, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub		
		switch (item.getItemId()) {
		case R.id.action_search:
			//handle event here
			Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
			return true;
		
		case R.id.action_add:			
			addEvent();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void addEvent(){
		if (getActivity() == null)
			return;
		CreateFragment fragment = new CreateFragment();
		
		MainActivity main = (MainActivity) getActivity();		
		main.switchView(fragment, FRAGMENT_NAME.CREATE, true);
	}

	private PagerAdapter buildAdapter() {
		
		mPagerAdapter = new EventPagerAdapter(getChildFragmentManager());				
		mPagerAdapter.addItem(EventFragment.newInstance(0), "My events");
		mPagerAdapter.addItem(EventFragment.newInstance(1), "Going");
		mPagerAdapter.addItem(EventFragment.newInstance(2), "Maybe");
		mPagerAdapter.addItem(EventFragment.newInstance(3), "Not Going");
		mPagerAdapter.addItem(EventFragment.newInstance(4), "Not Replied");
		
	    return mPagerAdapter;
	}

}
