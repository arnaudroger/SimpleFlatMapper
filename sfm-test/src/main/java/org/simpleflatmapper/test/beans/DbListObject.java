package org.simpleflatmapper.test.beans;

import java.util.List;

public class DbListObject {
	private int id;
	private List<DbObject> objects;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<DbObject> getObjects() {
		return objects;
	}
	public void setObjects(List<DbObject> objects) {
		this.objects = objects;
	}
}
