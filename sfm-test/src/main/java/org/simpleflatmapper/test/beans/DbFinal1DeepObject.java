package org.simpleflatmapper.test.beans;

public class DbFinal1DeepObject {

	private final int id;
	private final String value;
	private final DbFinalObject dbObject;
	
	public DbFinal1DeepObject(int id, String value, DbFinalObject dbObject) {
		super();
		this.id = id;
		this.value = value;
		this.dbObject = dbObject;
	}
	public int getId() {
		return id;
	}
	public String getValue() {
		return value;
	}
	public DbFinalObject getDbObject() {
		return dbObject;
	}
}
