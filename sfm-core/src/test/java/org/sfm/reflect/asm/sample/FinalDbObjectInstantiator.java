package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.map.getter.OrdinalEnumGetter;
import org.sfm.map.getter.StringEnumGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.primitive.LongGetter;

import java.io.InputStream;
import java.util.Map;

public final class FinalDbObjectInstantiator implements Instantiator<InputStream, DbFinalObject> {
	
	final LongGetter<InputStream> getter_id;
	final Getter<InputStream, String> getter_email;
	final Getter<InputStream, String> getter_name;
	final OrdinalEnumGetter<InputStream,Type> getter_typeOrdinal;
	final StringEnumGetter<InputStream,Type> getter_typeName;

	@SuppressWarnings("unchecked")
	public FinalDbObjectInstantiator(final Map<String, Getter<InputStream, ?>> injections) {
		this.getter_id = (LongGetter<InputStream>) injections.get("id");
		this.getter_email = (Getter<InputStream, String>) injections.get("email");
		this.getter_name = (Getter<InputStream, String>) injections.get("name");
		this.getter_typeOrdinal = (OrdinalEnumGetter<InputStream,Type>) injections.get("typeOrdinal");
		this.getter_typeName = (StringEnumGetter<InputStream,Type>) injections.get("typeName");
	}
	
	@Override
	public DbFinalObject newInstance(InputStream source) throws Exception {
		return new DbFinalObject(getter_id.getLong(source), getter_name.get(source), (String)getter_email.get(source), null, null, null);
	}
}
