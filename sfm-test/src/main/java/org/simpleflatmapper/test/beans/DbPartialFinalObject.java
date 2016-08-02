package org.simpleflatmapper.test.beans;

import org.simpleflatmapper.test.beans.DbObject.Type;

import java.util.Date;

public class DbPartialFinalObject {
	private long id;
	private String name;
	private final String email;
	private Date creationTime;
	
	private final Type typeOrdinal;
	private Type typeName;
	
	public DbPartialFinalObject(String email,  Type typeOrdinal) {
		this.email = email;
		this.typeOrdinal = typeOrdinal;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public void setTypeName(Type typeName) {
		this.typeName = typeName;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public Type getTypeOrdinal() {
		return typeOrdinal;
	}
	public Type getTypeName() {
		return typeName;
	}
}
