package csc.mobility.entity;

public class EventStatus {
	private String eid;
	private String rsvp_status;
	
	public EventStatus(String eid, String rsvp_status) {
		super();
		this.eid = eid;
		this.rsvp_status = rsvp_status;
	}
	
	public String getEid() {
		return eid;
	}
	
	public void setEid(String eid) {
		this.eid = eid;
	}
	
	public String getRsvp_status() {
		return rsvp_status;
	}
	
	public void setRsvp_status(String rsvp_status) {
		this.rsvp_status = rsvp_status;
	}
	
}
