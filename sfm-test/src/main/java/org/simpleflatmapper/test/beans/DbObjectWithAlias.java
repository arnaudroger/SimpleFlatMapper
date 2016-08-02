package org.simpleflatmapper.test.beans;

import org.simpleflatmapper.test.beans.DbObject.Type;

import javax.persistence.Column;
import java.util.Date;

public class DbObjectWithAlias {

	@Column(name="id")
	private long idWithAlias;
	private String nameWithAlias;
	private String emailWithAlias;
	private Date creationTimeWithAlias;

	private Type typeOrdinalWithAlias;
	private Type typeNameWithAlias;

	public long getIdWithAlias() {
		return idWithAlias;
	}
	public void setIdWithAlias(long idWithAlias) {
		this.idWithAlias = idWithAlias;
	}

	@Column(name="name")
	public String getNameWithAlias() {
		return nameWithAlias;
	}
	public void setNameWithAlias(String nameWithAlias) {
		this.nameWithAlias = nameWithAlias;
	}
	@Column(name="email")
	public String getEmailWithAlias() {
		return emailWithAlias;
	}
	public void setEmailWithAlias(String emailWithAlias) {
		this.emailWithAlias = emailWithAlias;
	}
	@Column(name="creation_time")
	public Date getCreationTimeWithAlias() {
		return creationTimeWithAlias;
	}
	public void setCreationTimeWithAlias(Date creationTimeWithAlias) {
		this.creationTimeWithAlias = creationTimeWithAlias;
	}
	@Column(name="type_ordinal")
	public Type getTypeOrdinalWithAlias() {
		return typeOrdinalWithAlias;
	}
	public void setTypeOrdinalWithAlias(Type typeOrdinalWithAlias) {
		this.typeOrdinalWithAlias = typeOrdinalWithAlias;
	}
	@Column(name="type_name")
	public Type getTypeNameWithAlias() {
		return typeNameWithAlias;
	}
	public void setTypeNameWithAlias(Type typeNameWithAlias) {
		this.typeNameWithAlias = typeNameWithAlias;
	}
	@Override
	public String toString() {
		return "DbObjectWithAlias [idWithAlias=" + idWithAlias
				+ ", nameWithAlias=" + nameWithAlias + ", emailWithAlias="
				+ emailWithAlias + ", creationTimeWithAlias="
				+ creationTimeWithAlias + ", typeOrdinalWithAlias="
				+ typeOrdinalWithAlias + ", typeNameWithAlias="
				+ typeNameWithAlias + "]";
	}
	
	
	
}
