package org.simpleflatmapper.test.beans;

import java.util.List;

public class DbFinalListObject {
	private final int id;
	private final List<DbObject> objects;
	
	public DbFinalListObject(int id, List<DbObject> objects) {
		super();
		this.id = id;
		this.objects = objects;
	}
	public int getId() {
		return id;
	}
	public List<DbObject> getObjects() {
		return objects;
	}
}
