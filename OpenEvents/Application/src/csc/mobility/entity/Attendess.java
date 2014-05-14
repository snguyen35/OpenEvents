package csc.mobility.entity;

public class Attendess {
	private String name;		
	private String imgURL;
	private String profileURL;
	
	public Attendess(String name, String imgURL, String profileURL) {
		super();
		this.name = name;
		this.imgURL = imgURL;
		this.profileURL = profileURL;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public String getProfileURL() {
		return profileURL;
	}
	public void setProfileURL(String profileURL) {
		this.profileURL = profileURL;
	}
	
}
