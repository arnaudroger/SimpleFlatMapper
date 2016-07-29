package org.simpleflatmapper.csv.impl.asm;

import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandlerFactory;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AsmFactory {
    private final ConcurrentMap<CsvMapperKey, Class<? extends CsvMapperCellHandlerFactory<?>>> csvMapperCache = new ConcurrentHashMap<CsvMapperKey, Class<? extends CsvMapperCellHandlerFactory<?>>>();


	public AsmFactory(ClassLoader cl) {
		factoryClassLoader = new FactoryClassLoader(cl);
	}

    @SuppressWarnings("unchecked")
    public <T> CsvMapperCellHandlerFactory<T> createCsvMapperCellHandler(Type target,
                                                                         DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters,
                                                                         Instantiator<CsvMapperCellHandler<T>, T> instantiator,
                                                                         CsvColumnKey[] keys,
                                                                         ParsingContextFactory parsingContextFactory,
                                                                         FieldMapperErrorHandler<CsvColumnKey> fieldErrorHandler,
                                                                         int maxMethodSize
                                                                         ) throws Exception {

        CsvMapperKey key = new CsvMapperKey(keys, setters, delayedCellSetterFactories, instantiator, target, fieldErrorHandler, maxMethodSize);

        Class<? extends CsvMapperCellHandlerFactory<?>> typeFactory = csvMapperCache.get(key);

        if (typeFactory == null) {
            final String className = generateClassNameCsvMapperCellHandler(target, delayedCellSetterFactories, setters);
            final String factoryName = className + "Factory";
            final byte[] bytes = CsvMapperCellHandlerBuilder.<T>createTargetSetterClass(className, delayedCellSetterFactories, setters, target, fieldErrorHandler == null || fieldErrorHandler instanceof RethrowFieldMapperErrorHandler, maxMethodSize);
            final byte[] bytesFactory = CsvMapperCellHandlerBuilder.createTargetSetterFactory(factoryName, className, target);
            createClass(className, bytes, target.getClass().getClassLoader());
            typeFactory = (Class<? extends CsvMapperCellHandlerFactory<?>>) createClass(factoryName, bytesFactory, target.getClass().getClassLoader());

            csvMapperCache.put(key, typeFactory);
        }

        return (CsvMapperCellHandlerFactory<T>) typeFactory
                .getConstructor(Instantiator.class, CsvColumnKey[].class, ParsingContextFactory.class, FieldMapperErrorHandler.class)
                .newInstance(instantiator, keys, parsingContextFactory, fieldErrorHandler);


    }


    private <T> String generateClassNameCsvMapperCellHandler(Type target, DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters) {
        StringBuilder sb = new StringBuilder();

        sb.append( "org.sfm.reflect.asm.")
                .append(getPackageName(target))
                .append(".AsmCsvMapperCellHandlerTo").append(TypeHelper.toClass(target).getSimpleName());
        if (delayedCellSetterFactories.length > 0) {
            sb.append("DS").append(Integer.toString(delayedCellSetterFactories.length));
        }
        if (setters.length > 0) {
            sb.append("S").append(Integer.toString(setters.length));
        }
        sb.append("_I").append(Long.toHexString(classNumber.getAndIncrement()));
        return sb.toString();
    }

}
