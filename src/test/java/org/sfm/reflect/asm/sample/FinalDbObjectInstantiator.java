package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.jdbc.impl.getter.LongResultSetGetter;
import org.sfm.jdbc.impl.getter.OrdinalEnumResultSetGetter;
import org.sfm.jdbc.impl.getter.StringEnumResultSetGetter;
import org.sfm.jdbc.impl.getter.StringResultSetGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

import java.sql.ResultSet;
import java.util.Map;

public final class FinalDbObjectInstantiator implements Instantiator<ResultSet, DbFinalObject> {
	
	final LongResultSetGetter getter_id;
	final Getter<ResultSet, ?> getter_email;
	final StringResultSetGetter getter_name;
	final OrdinalEnumResultSetGetter<Type> getter_typeOrdinal;
	final StringEnumResultSetGetter<Type> getter_typeName;
	
	@SuppressWarnings("unchecked")
	public FinalDbObjectInstantiator(final Map<String, Getter<ResultSet, ?>> injections) {
		this.getter_id = (LongResultSetGetter) injections.get("id");
		this.getter_email = (StringResultSetGetter) injections.get("email");
		this.getter_name = (StringResultSetGetter) injections.get("name");
		this.getter_typeOrdinal = (OrdinalEnumResultSetGetter<Type>) injections.get("typeOrdinal");
		this.getter_typeName = (StringEnumResultSetGetter<Type>) injections.get("typeNmae");
	}
	
	@Override
	public DbFinalObject newInstance(ResultSet source) throws Exception {
		return new DbFinalObject(getter_id.getLong(source), getter_name.get(source), (String)getter_email.get(source), null, null, null);
	}
}
