package csc.mobility.openevents.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import com.facebook.Session;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.helper.AppSharedPreferences;
import csc.mobility.openevents.R;
import csc.mobility.openevents.ui.MenuFragment.FBLogOutListener;

public class MainActivity extends BaseActivity implements FBLogOutListener{

	private Fragment mContent;
		
	public MainActivity(){
		super(R.string.app_name);
	}
		
	public int getScreenWidth(){
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		return width;
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
					
		//setSlidingActionBarEnabled(false);    		
		// init the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new SearchFragment();	
			//mContent=new SearchFragment();
		// set the Above View
		setContentView(R.layout.content_frame);
		
		//Show Map search at the first time app is open.
		switchMenu(mContent, FRAGMENT_NAME.SEARCH, true);
		
		// Monitor back stack changes to ensure the action bar title shows the appropriate       
		getSupportFragmentManager().addOnBackStackChangedListener(new OnBackStackChangedListener() {			
			@Override
			public void onBackStackChanged() {
				// TODO Auto-generated method stub
				int count = getSupportFragmentManager().getBackStackEntryCount();
				
				if(count==0){
					Intent setIntent = new Intent(Intent.ACTION_MAIN);
			        setIntent.addCategory(Intent.CATEGORY_HOME);
			        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        startActivity(setIntent); 
			        finish();
				}else{
//					Toast.makeText(getApplicationContext(), "count--"+count, Toast.LENGTH_SHORT).show();
//					
//					BackStackEntry root = getSupportFragmentManager().getBackStackEntryAt(0);
//					Toast.makeText(getApplicationContext(), "root--" + root.getName(), Toast.LENGTH_SHORT).show();
					
					BackStackEntry child = getSupportFragmentManager().getBackStackEntryAt(count-1);
					getActionBar().setTitle(child.getName());
					
				}		 
				
			}
		});        
	}
				
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);		
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}	
					
	public void switchMenu(Fragment fragment, CharSequence mTitle, boolean addToBackStack) {
		mContent = fragment;	
		if(mContent instanceof SearchFragment){
			set_TOUCHMODE_MARGIN();
		}else{
			set_TOUCHMODE_FULLSCREEN();
		}
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();		
		ft.replace(R.id.content_frame, fragment, mTitle.toString());
		
		if (addToBackStack) {			 
            ft.addToBackStack(mTitle.toString());
        }
		
		ft.commit();
		getSlidingMenu().showContent();
	}
		
	public void switchView(Fragment fragment, CharSequence mTitle, boolean addToBackStack) {		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
//		ft.setCustomAnimations(
//                R.animator.flip_right_in, R.animator.flip_right_out,
//                R.animator.flip_left_in, R.animator.flip_left_out);
		
		ft.replace(R.id.content_frame, fragment, mTitle.toString());
		
		if (addToBackStack) {
            ft.addToBackStack(mTitle.toString());
        }
		
		ft.commit();		
	}
	 
	 /**
	  * Logout From Facebook 
	  */
	@Override
	public void doLogout() {
		// TODO Auto-generated method stub
		Session session = Session.getActiveSession();
	    if (session != null) {
	        if (!session.isClosed()) {
	        	//clear preferences if saved
	            session.closeAndClearTokenInformation();	            
	        }
	    } else {

	        session = new Session(getApplicationContext());
	        Session.setActiveSession(session);
	        //clear preferences if saved
	        session.closeAndClearTokenInformation();	            
	    }
	    AppSharedPreferences prefs = new AppSharedPreferences();
	    prefs.setUserId(getApplicationContext(), "");
	    
	    Intent i = new Intent(MainActivity.this, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);       
        this.finish();	
	}
	
	public void set_TOUCHMODE_FULLSCREEN() {
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}
	
	public void set_TOUCHMODE_MARGIN() {
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}
	
	
}


