package org.simpleflatmapper.reflect.test.asm.sample;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;

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
