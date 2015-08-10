package org.simpleflatmapper.spring;


import org.springframework.jdbc.roma.api.config.provider.annotation.RowMapperField;

public class MappedObject4 {
	

	@RowMapperField(columnName="ID")
	private long id;

	@RowMapperField(columnName="YEAR_STARTED")
	private int yearStarted;
	@RowMapperField(columnName = "NAME")
	private String name;
	@RowMapperField(columnName="EMAIL")

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
	@Override
	public String toString() {
		return "MappedObject4 [id=" + id + ", yearStarted="
				+ yearStarted + ", name=" + name + ", email=" + email + "]";
	}
	
	
}
