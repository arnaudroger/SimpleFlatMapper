package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedGetter;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.cellreader.DelayedCellSetterImpl;
import org.sfm.csv.impl.primitive.BooleanDelayedGetter;
import org.sfm.reflect.asm.AsmFactory;

import java.sql.ResultSet;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

public class AsmInstantiatorTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	@Test
	public void testInstantiate() throws Exception {
		
		Instantiator<ResultSet, DbObject> instantiator = factory.createEmptyArgsInstatiantor(ResultSet.class, DbObject.class);
		
		assertNotNull(instantiator.newInstance(null));
	}

	@Test
	public void testInstantiateStringWithCharArray() throws Exception {
		HashMap<ConstructorParameter, Getter<DelayedCellSetter[], ?>> injections = new HashMap<ConstructorParameter, Getter<DelayedCellSetter[], ?>>();

		ConstructorParameter parameter = new ConstructorParameter("arg0", char[].class);
		DelayedGetter delayedGetter = new DelayedGetter(0);
		injections.put(parameter, delayedGetter);

		Instantiator<DelayedCellSetter[], String> instantiator =
				factory.createInstatiantor( DelayedCellSetter[].class,
						new ConstructorDefinition<String>(String.class.getConstructor(char[].class), parameter),
						injections);
		DelayedCellSetterImpl delayedCellSetter = new DelayedCellSetterImpl(null, new CellValueReader() {
			@Override
			public char[] read(char[] chars, int offset, int length, ParsingContext parsingContext) {
				return new char[]{'h'};
			}
		});
		delayedCellSetter.set(null, 0, 0, null);
		assertNotNull(instantiator.newInstance(new DelayedCellSetter[]{delayedCellSetter}));
	}
}
