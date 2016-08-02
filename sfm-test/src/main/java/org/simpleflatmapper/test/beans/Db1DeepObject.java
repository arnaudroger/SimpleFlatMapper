package org.simpleflatmapper.test.beans;

public class Db1DeepObject {

	private int id;
	private String value;
	private DbObject dbObject;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public DbObject getDbObject() {
		return dbObject;
	}
	public void setDbObject(DbObject dbObject) {
		this.dbObject = dbObject;
	}
}
