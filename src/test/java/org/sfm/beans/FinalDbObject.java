package org.sfm.beans;

import java.util.Date;

import org.sfm.beans.DbObject.Type;

public class FinalDbObject {
	private final long id;
	private final String name;
	private final String email;
	private final Date creationTime;
	
	private final Type typeOrdinal;
	private final Type typeName;
	
	public FinalDbObject(long id, String name, String email, Date creationTime, Type typeOrdinal, Type typeName) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.creationTime = creationTime;
		this.typeOrdinal = typeOrdinal;
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
