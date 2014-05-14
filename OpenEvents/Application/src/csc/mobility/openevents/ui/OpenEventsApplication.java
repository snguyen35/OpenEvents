package csc.mobility.openevents.ui;

import java.util.List;

import android.app.Application;

import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;

public class OpenEventsApplication extends Application{
		
	private GraphPlace selectedPlace;
	private List<GraphUser> selectedUsers;
	
	
	public List<GraphUser> getSelectedUsers() {
        return selectedUsers;
    }

	public void setSelectedUsers(List<GraphUser> users) {
        selectedUsers = users;
    }

    public GraphPlace getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(GraphPlace place) {
        this.selectedPlace = place;
    }
}
