package org.simpleflatmapper.test.beans;

import java.util.Set;

public class DbSetOfString {
	private int id;
	private Set<String> objects;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Set<String> getObjects() {
		return objects;
	}
	public void setObjects(Set<String> objects) {
		this.objects = objects;
	}
}
