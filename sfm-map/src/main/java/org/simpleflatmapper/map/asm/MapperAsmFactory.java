package org.simpleflatmapper.map.asm;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.AbstractMapper;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.asm.AsmFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class MapperAsmFactory {

    private final AsmFactory asmFactory;

	public MapperAsmFactory(AsmFactory asmFactory) {
        this.asmFactory = asmFactory;
	}


    private final Map<MapperKey, Constructor<? extends SourceMapper<?, ?>>> fieldMapperCache = new HashMap<MapperKey, Constructor<? extends SourceMapper<?, ?>>>();

    private <S, T> String generateClassNameForFieldMapper(final FieldMapper<S, T>[] mappers, final FieldMapper<S, T>[] constructorMappers, final Class<? super S> source, final Class<T> target) {
        StringBuilder sb = new StringBuilder();

        sb.append("org.simpleflatmapper.map.generated.");
        sb.append(asmFactory.getPackageName(target));
        sb.append(".AsmMapperFrom").append(asmFactory.replaceArray(source.getSimpleName()));
        sb.append("To").append(asmFactory.replaceArray(target.getSimpleName()));

        if (constructorMappers.length > 0) {
            sb.append("ConstInj").append(constructorMappers.length);
        }

        if (mappers.length > 0) {
            sb.append("Inj").append(mappers.length);
        }

        sb.append("_I").append(Long.toHexString(asmFactory.getNextClassNumber()));

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public <S, T> AbstractMapper<S, T> createMapper(final FieldKey<?>[] keys,
                                                    final FieldMapper<S, T>[] mappers,
                                                    final FieldMapper<S, T>[] constructorMappers,
                                                    final BiInstantiator<S, MappingContext<? super S>, T> instantiator,
                                                    final Class<? super S> source,
                                                    final Class<T> target) throws Exception {
        ClassLoader classLoader = target.getClassLoader();

        MapperKey key = MapperKey.of(keys, mappers, constructorMappers, instantiator, target, source);

        synchronized (fieldMapperCache) {
            Constructor<SourceMapper<S, T>> constructor = (Constructor<SourceMapper<S, T>>) fieldMapperCache.get(key);
            if (constructor == null) {

                final String className = generateClassNameForFieldMapper(mappers, constructorMappers, source, target);
                final byte[] bytes = MapperAsmBuilder.dump(className, mappers, constructorMappers, source, target);

                Class<SourceMapper<S, T>> type = (Class<SourceMapper<S, T>>) asmFactory.createClass(className, bytes, classLoader);
                constructor = (Constructor<SourceMapper<S, T>>) type.getDeclaredConstructors()[0];
                fieldMapperCache.put(key, constructor);
            }
            return (AbstractMapper<S, T>) constructor.newInstance(mappers, constructorMappers, instantiator);
        }
    }
}
