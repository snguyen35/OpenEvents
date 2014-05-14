package csc.mobility.openevents.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class EventPagerAdapter extends FragmentPagerAdapter{

	private List<Fragment> fragments;
	private List<String> titles;

	public EventPagerAdapter(FragmentManager mgr) {
		super(mgr);		
		this.fragments = new ArrayList<Fragment>();
		this.titles = new ArrayList<String>();
	}

	public void addItem(Fragment fragment, String title) {
		this.fragments.add(fragment);
		this.titles.add(title);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public String getPageTitle(int position) {
		return this.titles.get(position);
	}
}
