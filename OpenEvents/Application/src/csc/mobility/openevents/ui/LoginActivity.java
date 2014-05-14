package csc.mobility.openevents.ui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookRequestError;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import csc.mobility.constant.CONSTANTS;
import csc.mobility.helper.AppSharedPreferences;
import csc.mobility.openevents.R;

public class LoginActivity extends FragmentActivity {

	private LoginFragment loginFragment;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "csc.mobility.openevents", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
		
		setContentView(R.layout.activity_login);
		
		if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
			loginFragment = new LoginFragment();
	        getSupportFragmentManager().beginTransaction().add(R.id.container, loginFragment).commit();
	    } else {
	        // Or set the fragment from restored state info
	    	loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.container);
	    }
	}	

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent); 
        finish();
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class LoginFragment extends Fragment {
		
		private Activity mActivity;
		
		public LoginFragment() {
		}
		
		private static final String TAG = "LoginFragment";
		private static final List<String> PERMISSIONS 
			= Arrays.asList(CONSTANTS.READ_USER_EVENTS, CONSTANTS.READ_USER_FRIENDS);
		
		private UiLifecycleHelper uiHelper;
		private Session.StatusCallback callback = new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};
				
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			mActivity = (LoginActivity)getActivity();
			uiHelper = new UiLifecycleHelper(getActivity(), callback);
		    uiHelper.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
			View rootView = inflater.inflate(R.layout.fragment_login, container, false);										
						
			LoginButton authButton = (LoginButton) rootView.findViewById(R.id.authButton);			
			authButton.setReadPermissions(PERMISSIONS);
			authButton.setFragment(this);
			
			Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
			Settings.setPlatformCompatibilityEnabled(true);
			return rootView;
		}
				
		
		/********** Fragment lifecycle  Methods **********/
		@Override
		public void onResume() {
		    super.onResume();		    
		    Session session = Session.getActiveSession();
		    if (session != null &&
		           (session.isOpened() || session.isClosed()) ) {
		        onSessionStateChange(session, session.getState(), null);
		    }
		    uiHelper.onResume();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);
		    uiHelper.onActivityResult(requestCode, resultCode, data);
//		    if(resultCode == RESULT_OK){
//		    	Intent i = new Intent(getActivity(), MainActivity.class);
//				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(i);	
//		    }
		}

		@Override
		public void onPause() {
		    super.onPause();
		    uiHelper.onPause();
		}

		@Override
		public void onDestroy() {
		    super.onDestroy();
		    uiHelper.onDestroy();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
		    super.onSaveInstanceState(outState);
		    uiHelper.onSaveInstanceState(outState);
		}
		
		/********** Other Methods **********/
		private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		    if (state.isOpened()) {
		    	Log.i(TAG, "Facebook session opened. /nLogged in...");
		    	makeMeRequest(session);		        
		    } else if (state.isClosed()) {
		        Log.i(TAG, "Facebook session closed. /nLogged out...");
		    }
		}
	
		private void makeMeRequest(final Session session) {
			Request request = Request.newMeRequest(session,
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (session == Session.getActiveSession()) {
								if (user != null) {								
									AppSharedPreferences prefs = new AppSharedPreferences();
									prefs.setUserId(mActivity, user.getId());								
									Intent i = new Intent(mActivity, MainActivity.class);
									i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(i);
									//mActivity.finish();
								}
							}
							if (response.getError() != null) {
								handleError(response.getError());
							}
						}

						
					});
			request.executeAsync();
		}

		private void handleError(FacebookRequestError error) {
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
	//End fragment
	
	

}
