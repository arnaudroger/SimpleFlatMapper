package org.sfm.beans;

import java.util.Date;
import java.util.Random;

public class DbObject {

	private static Random random = new Random();
	public static DbObject newInstance() {
		DbObject object = new DbObject();
		object.setId(random.nextInt());
		object.setName("name" + Long.toHexString(object.getId()));
		object.setEmail("email" + Long.toHexString(object.getId()));
		object.setCreationTime(new Date());
		object.setTypeName(Type.values()[(int)(Math.abs(object.getId()) % 4l)]);
		object.setTypeOrdinal(Type.values()[(int)(Math.abs(object.getId()) % 4l)]);
		return object;
	}

	public enum Type {
		type1, type2, type3, type4
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DbObject dbObject = (DbObject) o;

		if (id != dbObject.id) return false;
		if (name != null ? !name.equals(dbObject.name) : dbObject.name != null) return false;
		if (email != null ? !email.equals(dbObject.email) : dbObject.email != null) return false;
		if (creationTime != null ? creationTime.getTime() != dbObject.creationTime.getTime() : dbObject.creationTime != null)
			return false;
		if (typeOrdinal != dbObject.typeOrdinal) return false;
		return typeName == dbObject.typeName;

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
}
