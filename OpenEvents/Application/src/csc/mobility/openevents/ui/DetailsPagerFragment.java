package csc.mobility.openevents.ui;

import csc.mobility.constant.CONSTANTS;
import csc.mobility.entity.Event;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.adapter.EventPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DetailsPagerFragment extends BaseFragment{
	
	View rootView;	
	private ViewPager mViewPager;
    private EventPagerAdapter mPagerAdapter;     
    private PagerTabStrip  tabStrip;
    private Event eventItem;
    private String eventId;
    
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);	
		Bundle args = getArguments();
		if (args!=null) {
			eventItem = (Event) args.getParcelable(CONSTANTS.KEY_EVENT_ITEM);
			eventId=eventItem.getId();					
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub				
		rootView = inflater.inflate(R.layout.fragment_details_pager, container, false);
						
		// Initilization	
		tabStrip = (PagerTabStrip)rootView.findViewById(R.id.pager_header);
		tabStrip.setVisibility(View.GONE);
		
		mViewPager=(ViewPager)rootView.findViewById(R.id.fragment_details_pager);
		mViewPager.setAdapter(buildAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {					
			@Override
			public void onPageScrollStateChanged(int arg0) { }

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }

			@Override
			public void onPageSelected(int position) {
								
				switch (position) {
				case 0:
					((MainActivity) getActivity()).set_TOUCHMODE_FULLSCREEN();
					tabStrip.setVisibility(View.GONE);					
					break;
				default:
					((MainActivity) getActivity()).set_TOUCHMODE_MARGIN();
					tabStrip.setVisibility(View.VISIBLE);					
					break;
				}
			}
		});	
		mViewPager.setCurrentItem(0);
		
		return rootView;
	}
	
	

	private PagerAdapter buildAdapter() {
		
		mPagerAdapter = new EventPagerAdapter(getChildFragmentManager());				
		mPagerAdapter.addItem(DetailsFragment.newInstance(eventItem), "");
		mPagerAdapter.addItem(AttendeeFragment.newInstance(eventId), "ATTENDEES");	
		
	    return mPagerAdapter;
	}
	
	
}
