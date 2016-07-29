package org.simpleflatmapper.csv.impl.asm;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;
import org.sfm.csv.impl.*;
import org.sfm.csv.impl.cellreader.DelayedCellSetterImpl;
import org.sfm.csv.mapper.CellSetter;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.reflect.ExecutableInstantiatorDefinition;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.asm.AsmFactory;

import java.sql.ResultSet;
import java.util.HashMap;

import static org.junit.Assert.assertNotNull;

public class CsvMapperCellHandlerImpAsmInstantiatorTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	@Test
	public void testInstantiate() throws Exception {
		
		Instantiator<ResultSet, DbObject> instantiator = factory.createEmptyArgsInstantiator(ResultSet.class, DbObject.class);
		
		Assert.assertNotNull(instantiator.newInstance(null));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInstantiateStringWithCharArray() throws Exception {
		HashMap<Parameter, Getter<? super CsvMapperCellHandlerImpl<String>, ?>> injections = new HashMap<Parameter, Getter<? super CsvMapperCellHandlerImpl<String>, ?>>();

		Parameter parameter = new Parameter(0, "arg0", char[].class);
		DelayedGetter delayedGetter = new DelayedGetter(0);
		injections.put(parameter, delayedGetter);

		Instantiator<CsvMapperCellHandlerImpl<String>, String> instantiator =
				factory.createInstantiator(CsvMapperCellHandlerImpl.class,
						new ExecutableInstantiatorDefinition(String.class.getConstructor(char[].class), parameter),
						injections);
		DelayedCellSetterImpl delayedCellSetter = new DelayedCellSetterImpl(null, new CellValueReader() {
			@Override
			public char[] read(char[] chars, int offset, int length, ParsingContext parsingContext) {
				return new char[]{'h'};
			}
		});
		delayedCellSetter.set(null, 0, 0, null);

        CsvMapperCellHandlerImpl targetSettersImpl = new CsvMapperCellHandlerImpl(instantiator, new DelayedCellSetter[]{delayedCellSetter}, new CellSetter[]{}, null, null, null);

		Assert.assertNotNull(instantiator.newInstance(targetSettersImpl));
	}
}
