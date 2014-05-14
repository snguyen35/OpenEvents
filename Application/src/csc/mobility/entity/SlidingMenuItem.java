package csc.mobility.entity;

public class SlidingMenuItem {
	
	private int id;
	private int icon ;
    private String name ;
    private String num;
    private boolean isCounterVisible = false;
    
	

	public SlidingMenuItem(int id, int icon, String name) {
		super();
		this.id = id;
		this.icon = icon;
		this.name = name;
	}

	public SlidingMenuItem(int id, int icon, String name, String num,
			boolean isCounterVisible) {
		super();
		this.id = id;
		this.icon = icon;
		this.name = name;
		this.num = num;
		this.isCounterVisible = isCounterVisible;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public boolean isCounterVisible() {
		return isCounterVisible;
	}

	public void setCounterVisible(boolean isCounterVisible) {
		this.isCounterVisible = isCounterVisible;
	}
	
}
