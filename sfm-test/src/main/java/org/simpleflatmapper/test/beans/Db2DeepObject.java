package org.simpleflatmapper.test.beans;

public class Db2DeepObject {

	private int id;
	private Db1DeepObject db1Object;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Db1DeepObject getDb1Object() {
		return db1Object;
	}
	public void setDb1Object(Db1DeepObject db1Object) {
		this.db1Object = db1Object;
	}
}
