package org.simpleflatmapper.csv.impl.asm;

import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.error.RethrowFieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContextFactory;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandlerFactory;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CsvAsmFactory {

    private final AsmFactory asmFactory;
    private final ConcurrentMap<CsvMapperKey, Class<? extends CsvMapperCellHandlerFactory<?>>> csvMapperCache = new ConcurrentHashMap<CsvMapperKey, Class<? extends CsvMapperCellHandlerFactory<?>>>();

	public CsvAsmFactory(AsmFactory asmFactory) {
        this.asmFactory = asmFactory;
	}

    @SuppressWarnings("unchecked")
    public <T> CsvMapperCellHandlerFactory<T> createCsvMapperCellHandler(Type target,
                                                                         DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories,
                                                                         CellSetter<T>[] setters,
                                                                         Instantiator<CsvMapperCellHandler<T>, T> instantiator,
                                                                         CsvColumnKey[] keys,
                                                                         ParsingContextFactory parsingContextFactory,
                                                                         FieldMapperErrorHandler<? super CsvColumnKey> fieldErrorHandler,
                                                                         int maxMethodSize
                                                                         ) throws Exception {

        CsvMapperKey key = new CsvMapperKey(keys, setters, delayedCellSetterFactories, instantiator, target, fieldErrorHandler, maxMethodSize);

        Class<? extends CsvMapperCellHandlerFactory<?>> typeFactory = csvMapperCache.get(key);

        if (typeFactory == null) {
            final String className = generateClassNameCsvMapperCellHandler(target, delayedCellSetterFactories, setters);
            final String factoryName = className + "Factory";
            final byte[] bytes = CsvMapperCellHandlerBuilder.<T>createTargetSetterClass(className, delayedCellSetterFactories, setters, target, fieldErrorHandler == null || fieldErrorHandler instanceof RethrowFieldMapperErrorHandler, maxMethodSize);
            final byte[] bytesFactory = CsvMapperCellHandlerBuilder.createTargetSetterFactory(factoryName, className, target);
            asmFactory.createClass(className, bytes, target.getClass().getClassLoader());
            typeFactory = (Class<? extends CsvMapperCellHandlerFactory<?>>) asmFactory.createClass(factoryName, bytesFactory, target.getClass().getClassLoader());

            csvMapperCache.put(key, typeFactory);
        }

        return (CsvMapperCellHandlerFactory<T>) typeFactory
                .getConstructor(Instantiator.class, CsvColumnKey[].class, ParsingContextFactory.class, FieldMapperErrorHandler.class)
                .newInstance(instantiator, keys, parsingContextFactory, fieldErrorHandler);


    }


    private <T> String generateClassNameCsvMapperCellHandler(Type target, DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, CellSetter<T>[] setters) {
        StringBuilder sb = new StringBuilder();

        sb.append( "org.simpleflatmapper.csv.generated.")
                .append(asmFactory.getPackageName(target))
                .append(".AsmCsvMapperCellHandlerTo").append(asmFactory.replaceArray(TypeHelper.toClass(target).getSimpleName()));
        if (delayedCellSetterFactories.length > 0) {
            sb.append("DS").append(Integer.toString(delayedCellSetterFactories.length));
        }
        if (setters.length > 0) {
            sb.append("S").append(Integer.toString(setters.length));
        }
        sb.append("_I").append(Long.toHexString(asmFactory.getNextClassNumber()));
        return sb.toString();
    }

}
