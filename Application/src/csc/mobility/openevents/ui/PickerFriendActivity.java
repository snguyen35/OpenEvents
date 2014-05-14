package csc.mobility.openevents.ui;

import java.util.List;

import com.facebook.FacebookException;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;

import csc.mobility.openevents.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

public class PickerFriendActivity extends FragmentActivity {
	private FriendPickerFragment friendPickerFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picker_friend);

		FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            // First time through, we create our fragment programmatically.
            final Bundle args = getIntent().getExtras();
            friendPickerFragment = new FriendPickerFragment(args);
            fm.beginTransaction()
                    .add(R.id.friend_picker_fragment, friendPickerFragment)
                    .commit();
        } else {
            // Subsequent times, our fragment is recreated by the framework and already has saved and
            // restored its state, so we don't need to specify args again. (In fact, this might be
            // incorrect if the fragment was modified programmatically since it was created.)
            friendPickerFragment = (FriendPickerFragment) fm.findFragmentById(R.id.friend_picker_fragment);
        }

        friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
            @Override
            public void onError(PickerFragment<?> fragment, FacebookException error) {
                PickerFriendActivity.this.onError(error);
            }
        });

        friendPickerFragment.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
            @Override
            public void onDoneButtonClicked(PickerFragment<?> fragment) {
                // We just store our selection in the Application for other activities to look at.
            	OpenEventsApplication application = (OpenEventsApplication) getApplication();
                application.setSelectedUsers(friendPickerFragment.getSelection());

                setResult(RESULT_OK, null);
                finish();
            }
        });
    }

		

	private void onError(Exception error) {
        String text = getString(R.string.exception, error.getMessage());
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            OpenEventsApplication application = (OpenEventsApplication) getApplication();
            List<GraphUser> selectedUsers = application.getSelectedUsers();
            if (selectedUsers != null && !selectedUsers.isEmpty()) {
                friendPickerFragment.setSelection(selectedUsers);
            }
            // Load data, unless a query has already taken place.
            friendPickerFragment.loadData(false);
        } catch (Exception ex) {
            onError(ex);
        }
    }

}
