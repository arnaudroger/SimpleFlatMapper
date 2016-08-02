package org.simpleflatmapper.core.reflect.asm.sample;

import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.primitive.CharacterSetter;

public class PrimitiveCharacterSetter implements Setter<DbPrimitiveObjectWithSetter, Character>, CharacterSetter<DbPrimitiveObjectWithSetter> {

	@Override
	public void setCharacter(DbPrimitiveObjectWithSetter target, char value) throws Exception {
		target.setpCharacter(value);
	}

	@Override
	public void set(DbPrimitiveObjectWithSetter target, Character value) throws Exception {
		target.setpInt(value.charValue());
	}

}
