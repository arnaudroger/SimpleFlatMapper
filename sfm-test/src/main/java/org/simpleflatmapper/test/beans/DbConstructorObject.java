package org.simpleflatmapper.test.beans;

import java.util.Date;

public class DbConstructorObject {
	private final String prop1;
	private final String prop2;
	private final Date prop3;
	
	private final int c;
	
	public String getProp1() {
		return prop1;
	}
	public String getProp2() {
		return prop2;
	}
	public Date getProp3() {
		return prop3;
	}
	public int getC() {
		return c;
	}
	
	public DbConstructorObject(String prop1) {
		this.prop1 = prop1;
		this.prop2 = null;
		this.prop3 = null;
		c = 0;
	}
	
	public DbConstructorObject(String prop1, String prop2) {
		this.prop1 = prop1;
		this.prop2 = prop2;
		this.prop3 = null;
		c = 1;
	}

	public DbConstructorObject(String prop1, Date prop3) {
		this.prop1 = prop1;
		this.prop2 = null;
		this.prop3 = prop3;
		c = 2;
	}

}
