package org.simpleflatmapper.test.beans;

import java.util.List;

public class DbListOfString {
	private int id;
	private List<String> objects;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getObjects() {
		return objects;
	}
	public void setObjects(List<String> objects) {
		this.objects = objects;
	}
}
