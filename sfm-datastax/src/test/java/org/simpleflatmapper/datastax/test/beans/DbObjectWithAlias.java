package org.simpleflatmapper.datastax.test.beans;

import com.datastax.driver.mapping.annotations.Column;
import org.simpleflatmapper.test.beans.DbObject.Type;

import java.util.Date;

public class DbObjectWithAlias {

	@Column(name="id")
	private long idWithAlias;
	@Column(name="name")
	private String nameWithAlias;

	@Column(name="email")
	private String emailWithAlias;
	@Column(name="creation_time")
	private Date creationTimeWithAlias;

	@Column(name="type_ordinal")
	private Type typeOrdinalWithAlias;
	@Column(name="type_name")
	private Type typeNameWithAlias;

	public long getIdWithAlias() {
		return idWithAlias;
	}
	public void setIdWithAlias(long idWithAlias) {
		this.idWithAlias = idWithAlias;
	}

	public String getNameWithAlias() {
		return nameWithAlias;
	}
	public void setNameWithAlias(String nameWithAlias) {
		this.nameWithAlias = nameWithAlias;
	}
	public String getEmailWithAlias() {
		return emailWithAlias;
	}
	public void setEmailWithAlias(String emailWithAlias) {
		this.emailWithAlias = emailWithAlias;
	}
	public Date getCreationTimeWithAlias() {
		return creationTimeWithAlias;
	}
	public void setCreationTimeWithAlias(Date creationTimeWithAlias) {
		this.creationTimeWithAlias = creationTimeWithAlias;
	}
	public Type getTypeOrdinalWithAlias() {
		return typeOrdinalWithAlias;
	}
	public void setTypeOrdinalWithAlias(Type typeOrdinalWithAlias) {
		this.typeOrdinalWithAlias = typeOrdinalWithAlias;
	}
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
