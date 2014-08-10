package org.sfm.beans;


public class SmallBenchmarkObject {
	
	private long id;
	private int yearStarted;
	private String name;
	private String email;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getYearStarted() {
		return yearStarted;
	}
	public void setYearStarted(int yearStarted) {
		this.yearStarted = yearStarted;
	}
}
