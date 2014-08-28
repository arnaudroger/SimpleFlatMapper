package org.sfm.reflect.asm.sample;

import java.sql.ResultSet;
import java.util.Map;

import org.sfm.beans.DbObject.Type;
import org.sfm.beans.DbFinalObject;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringEnumIndexedResultSetGetter;
import org.sfm.jdbc.getter.StringIndexedResultSetGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

public final class FinalDbObjectInstantiator implements Instantiator<ResultSet, DbFinalObject> {
	
	final LongIndexedResultSetGetter getter_id;
	final Getter<ResultSet, ?> getter_email;
	final StringIndexedResultSetGetter getter_name;
	final OrdinalEnumIndexedResultSetGetter<Type> getter_typeOrdinal;
	final StringEnumIndexedResultSetGetter<Type> getter_typeName;
	
	@SuppressWarnings("unchecked")
	public FinalDbObjectInstantiator(final Map<String, Getter<ResultSet, ?>> injections) {
		this.getter_id = (LongIndexedResultSetGetter) injections.get("id");
		this.getter_email = (StringIndexedResultSetGetter) injections.get("email");
		this.getter_name = (StringIndexedResultSetGetter) injections.get("name");
		this.getter_typeOrdinal = (OrdinalEnumIndexedResultSetGetter<Type>) injections.get("typeOrdinal");
		this.getter_typeName = (StringEnumIndexedResultSetGetter<Type>) injections.get("typeNmae");
	}
	
	@Override
	public DbFinalObject newInstance(ResultSet source) throws Exception {
		return new DbFinalObject(getter_id.getLong(source), getter_name.get(source), (String)getter_email.get(source), null, null, null);
	}
}
