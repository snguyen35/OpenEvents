package csc.mobility.openevents.ui;

import csc.mobility.openevents.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationFragment extends Fragment {
	private View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		rootView = inflater.inflate(R.layout.fragment_notification, container, false);	

		TextView tv = (TextView) rootView.findViewById(R.id.tvNotification);
		tv.setText("Will be implemented on next phase");
		return rootView;
	}

}
