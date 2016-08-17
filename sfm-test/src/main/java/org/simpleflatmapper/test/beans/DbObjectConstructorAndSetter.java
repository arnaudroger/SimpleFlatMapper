package org.simpleflatmapper.test.beans;

import org.simpleflatmapper.test.beans.DbObject.Type;

import java.util.Date;

public class DbObjectConstructorAndSetter {
	private long id;
	private String name;
	private String email;
	private Date creationTime;

	private Type typeOrdinal;
	private Type typeName;


	public DbObjectConstructorAndSetter(long id, String name, String email, Date creationTime, Type typeOrdinal, Type typeName) {
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

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DbObjectConstructorAndSetter that = (DbObjectConstructorAndSetter) o;

		if (id != that.id) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (email != null ? !email.equals(that.email) : that.email != null) return false;
		if (creationTime != null ? !creationTime.equals(that.creationTime) : that.creationTime != null) return false;
		if (typeOrdinal != that.typeOrdinal) return false;
		return typeName == that.typeName;

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
		result = 31 * result + (typeOrdinal != null ? typeOrdinal.hashCode() : 0);
		result = 31 * result + (typeName != null ? typeName.hashCode() : 0);
		return result;
	}

	public static DbObjectConstructorAndSetter newInstance() {
		DbObject dbObject = DbObject.newInstance();
		return new DbObjectConstructorAndSetter(dbObject.getId(), dbObject.getName(), dbObject.getEmail(), dbObject.getCreationTime(), dbObject.getTypeOrdinal(), dbObject.getTypeName());
	}
}
