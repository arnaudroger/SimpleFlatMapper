package org.simpleflatmapper.test.beans;

public class Db1DeepPartialObject {

	private int id;
	private String value;
	private DbPartialFinalObject dbObject;
	
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
	public DbPartialFinalObject getDbObject() {
		return dbObject;
	}
	public void setDbObject(DbPartialFinalObject dbObject) {
		this.dbObject = dbObject;
	}
}
