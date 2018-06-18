package org.simpleflatmapper.csv.test.impl.asm;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.*;
import org.simpleflatmapper.csv.impl.cellreader.DelayedCellSetterImpl;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.asm.AsmFactory;

import java.io.InputStream;
import java.util.HashMap;


public class CsvMapperCellHandlerImpAsmInstantiatorTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	@Test
	public void testInstantiate() throws Exception {
		
		Instantiator<InputStream, DbObject> instantiator = factory.createEmptyArgsInstantiator(InputStream.class, DbObject.class);
		
		Assert.assertNotNull(instantiator.newInstance(null));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testInstantiateStringWithCharArray() throws Exception {
		HashMap<Parameter, Getter<? super CsvMapperCellHandlerImpl<String>, ?>> injections = new HashMap<Parameter, Getter<? super CsvMapperCellHandlerImpl<String>, ?>>();

		Parameter parameter = new Parameter(0, "arg0", char[].class);
		DelayedGetter delayedGetter = new DelayedGetter(0);
		injections.put(parameter, delayedGetter);

		Class<CsvMapperCellHandlerImpl<String>> source = (Class)CsvMapperCellHandlerImpl.class;
		Instantiator<CsvMapperCellHandlerImpl<String>, String> instantiator =
				factory.createInstantiator(source,
						new ExecutableInstantiatorDefinition(String.class.getConstructor(char[].class), parameter),
						injections, true);
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
