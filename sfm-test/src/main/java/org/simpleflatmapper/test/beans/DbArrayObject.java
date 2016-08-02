package org.simpleflatmapper.test.beans;


public class DbArrayObject {
	private int id;
	private DbObject[] objects;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public DbObject[] getObjects() {
		return objects;
	}
	public void setObjects(DbObject[] objects) {
		this.objects = objects;
	}
}
