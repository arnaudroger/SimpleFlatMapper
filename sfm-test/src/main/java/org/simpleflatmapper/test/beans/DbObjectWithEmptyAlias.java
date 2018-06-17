package org.simpleflatmapper.test.beans;

import org.simpleflatmapper.test.beans.DbObject.Type;

import javax.persistence.Column;
import java.util.Date;

public class DbObjectWithEmptyAlias {

	@Column()
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

	@Column()
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column()
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Column()
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	@Column()
	public Type getTypeOrdinal() {
		return typeOrdinal;
	}
	public void setTypeOrdinal(Type typeOrdinal) {
		this.typeOrdinal = typeOrdinal;
	}
	@Column()
	public Type getTypeName() {
		return typeName;
	}
	public void setTypeName(Type typeName) {
		this.typeName = typeName;
	}
	@Override
	public String toString() {
		return "DbObject [id=" + id
				+ ", name=" + name + ", email="
				+ email + ", creationTime="
				+ creationTime + ", typeOrdinal="
				+ typeOrdinal + ", typeName="
				+ typeName + "]";
	}
	
	
	
}
