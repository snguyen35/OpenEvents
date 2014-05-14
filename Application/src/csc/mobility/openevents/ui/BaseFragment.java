package csc.mobility.openevents.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.facebook.FacebookRequestError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import csc.mobility.constant.CONSTANTS;
import csc.mobility.constant.FRAGMENT_NAME;
import csc.mobility.entity.Event;
import csc.mobility.openevents.R;

public class BaseFragment extends Fragment{
	
	public UiLifecycleHelper uiHelper;	
	public MainActivity mActivity;
	
	public Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state,
				final Exception exception) {			
			updateView();
		}
	};
	
	public void updateView(){				
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);	        
	    mActivity = (MainActivity)getActivity();
	    uiHelper = new UiLifecycleHelper(mActivity, statusCallback);
	    uiHelper.onCreate(savedInstanceState);	    
	}	    
	    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);	       
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();	    
	}
	
	public void switchView(Fragment toFragment, CharSequence actionBarTitle, boolean addToBackStack, Event item) {
		if (mActivity == null)
			return;	
				
		Bundle args = new Bundle();
		args.putParcelable(CONSTANTS.KEY_EVENT_ITEM, item);		
		toFragment.setArguments(args);
		
		mActivity.switchView(toFragment, actionBarTitle, addToBackStack);		
	}
	
	public void switchToDetailView(String fromFragment, boolean addToBackStack, Event item) {
		if (mActivity == null)
			return;	
			
		Bundle args = new Bundle();
		args.putParcelable(CONSTANTS.KEY_EVENT_ITEM, item);		
		args.putString(CONSTANTS.KEY_FRAGMENT_NAME, fromFragment);
		
		DetailsFragment fragment = new DetailsFragment();			
		fragment.setArguments(args);
					
		mActivity.switchView(fragment, FRAGMENT_NAME.DETAILS, addToBackStack);		
	}
	
	public void handleError(FacebookRequestError error) {
        DialogInterface.OnClickListener listener = null;
        String dialogBody = null;

        if (error == null) {
            dialogBody = getString(R.string.error_dialog_default_text);
        } else {
            switch (error.getCategory()) {
                case AUTHENTICATION_RETRY:
                    // tell the user what happened by getting the message id, and
                    // retry the operation later
                    String userAction = (error.shouldNotifyUser()) ? "" :
                            getString(error.getUserActionMessageId());
                    dialogBody = getString(R.string.error_authentication_retry, userAction);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, CONSTANTS.M_FACEBOOK_URL);
                            startActivity(intent);
                        }
                    };
                    break;

                case AUTHENTICATION_REOPEN_SESSION:
                    // close the session and reopen it.
                    dialogBody = getString(R.string.error_authentication_reopen);
                    listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Session session = Session.getActiveSession();
                            if (session != null && !session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        }
                    };
                    break;

//                case PERMISSION:
//                    // request the publish permission
//                    dialogBody = getString(R.string.error_permission);
//                    listener = new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {                            
//                            requestPublishPermissions(Session.getActiveSession());
//                        }
//                    };
//                    break;

                case SERVER:
                case THROTTLING:
                    // this is usually temporary, don't clear the fields, and
                    // ask the user to try again
                    dialogBody = getString(R.string.error_server);
                    break;

                case BAD_REQUEST:
                    // this is likely a coding error, ask the user to file a bug
                    dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
                    break;

                case OTHER:
                case CLIENT:
                default:
                    // an unknown issue occurred, this could be a code error, or
                    // a server side issue, log the issue, and either ask the
                    // user to retry, or file a bug
                    dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
                    break;
            }
        }

        new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.error_dialog_button_text, listener)
                .setTitle(R.string.error_dialog_title)
                .setMessage(dialogBody)
                .show();
    }

	
}
