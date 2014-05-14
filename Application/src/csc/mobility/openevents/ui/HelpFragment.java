package csc.mobility.openevents.ui;

import csc.mobility.openevents.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment {
	private View rootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
				
		rootView = inflater.inflate(R.layout.fragment_help, container, false);	

		
		return rootView;
	}

}
