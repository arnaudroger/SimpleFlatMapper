package org.sfm.reflect.asm.sample;

import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.primitive.CharacterSetter;

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
