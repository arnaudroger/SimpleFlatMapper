package org.sfm.beans;

import java.util.Date;

import org.sfm.beans.DbObject.Type;

public class DbPartialFinalObject {
	private final long id;
	private String name;
	private final String email;
	private Date creationTime;
	
	private Type typeOrdinal;
	private Type typeName;
	
	
	public DbPartialFinalObject(long id,String email) {
		this.id = id;
		this.email = email;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public void setTypeOrdinal(Type typeOrdinal) {
		this.typeOrdinal = typeOrdinal;
	}

	public void setTypeName(Type typeName) {
		this.typeName = typeName;
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
