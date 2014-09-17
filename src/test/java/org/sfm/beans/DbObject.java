package org.sfm.beans;

import java.util.Date;

public class DbObject {
	
	public enum Type {
		type1, type2, type3, type4;
	}
	private long id;
	private String name;
	private String email;
	private Date creationTime;
	
	private Type typeOrdinal;
	private Type typeName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Type getTypeOrdinal() {
		return typeOrdinal;
	}
	public void setTypeOrdinal(Type typeOrdinal) {
		this.typeOrdinal = typeOrdinal;
	}
	public Type getTypeName() {
		return typeName;
	}
	public void setTypeName(Type typeName) {
		this.typeName = typeName;
	}
	@Override
	public String toString() {
		return "DbObject [id=" + id + ", name=" + name + ", email=" + email
				+ ", creationTime=" + creationTime + ", typeOrdinal="
				+ typeOrdinal + ", typeName=" + typeName + "]";
	}
	
}
