package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.jdbc.impl.getter.LongResultSetGetter;
import org.sfm.jdbc.impl.getter.StringResultSetGetter;
import org.sfm.map.impl.getter.OrdinalEnumGetter;
import org.sfm.map.impl.getter.StringEnumGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

import java.sql.ResultSet;
import java.util.Map;

public final class FinalDbObjectInstantiator implements Instantiator<ResultSet, DbFinalObject> {
	
	final LongResultSetGetter getter_id;
	final StringResultSetGetter getter_email;
	final StringResultSetGetter getter_name;
	final OrdinalEnumGetter<ResultSet,Type> getter_typeOrdinal;
	final StringEnumGetter<ResultSet,Type> getter_typeName;

	@SuppressWarnings("unchecked")
	public FinalDbObjectInstantiator(final Map<String, Getter<ResultSet, ?>> injections) {
		this.getter_id = (LongResultSetGetter) injections.get("id");
		this.getter_email = (StringResultSetGetter) injections.get("email");
		this.getter_name = (StringResultSetGetter) injections.get("name");
		this.getter_typeOrdinal = (OrdinalEnumGetter<ResultSet,Type>) injections.get("typeOrdinal");
		this.getter_typeName = (StringEnumGetter<ResultSet,Type>) injections.get("typeName");
	}
	
	@Override
	public DbFinalObject newInstance(ResultSet source) throws Exception {
		return new DbFinalObject(getter_id.getLong(source), getter_name.get(source), (String)getter_email.get(source), null, null, null);
	}
}
