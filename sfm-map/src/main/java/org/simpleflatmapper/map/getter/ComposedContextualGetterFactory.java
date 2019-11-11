package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

import java.lang.reflect.Type;

public class ComposedContextualGetterFactory<T, K>  implements ContextualGetterFactory<T, K> {

    private final ContextualGetterFactory<? super T, K>[] factories;

    private ComposedContextualGetterFactory(ContextualGetterFactory<? super T, K>[] factories) {
        this.factories = factories;
    }

    @Override
    public <P> ContextualGetter<T, P> newGetter(Type target, K key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
        ContextualGetterFactory[] factories = this.factories;

        for(int i = 0; i < factories.length; i++) {
            ContextualGetterFactory<T, K> factory = factories[i];
            ContextualGetter<T, P> getter = factory.newGetter(target, key, mappingContextFactoryBuilder, properties);
            if (getter != null) return getter;
        }

        return null;
    }

    public  static <T, K> ComposedContextualGetterFactory<T, K> composed(ContextualGetterFactory<? super T, K> cfg1, ContextualGetterFactory<? super T, K> cfg2) {
        int size = getSize(cfg1) + getSize(cfg2);
        ContextualGetterFactory<? super T, K>[] factories = new ContextualGetterFactory[size];

        int index = 0;

        index = append(factories, 0, cfg1);
        index = append(factories, index, cfg2);

        return new ComposedContextualGetterFactory<T, K>(factories);

    }

    private static <T, K> int append(ContextualGetterFactory<? super T, K>[] factories, int from, ContextualGetterFactory<? super T, K> cfg) {
        if (cfg instanceof ComposedContextualGetterFactory) {
            ContextualGetterFactory<? super T, K>[] fs = ((ComposedContextualGetterFactory)cfg).factories;
            System.arraycopy(fs, 0, factories, from, fs.length);
            return from + fs.length;
        } else {
            factories[from] = cfg;
            return from + 1;
        }
    }

    private static int getSize(ContextualGetterFactory<?, ?> cfg) {
        if (cfg instanceof ComposedContextualGetterFactory) {
            return ((ComposedContextualGetterFactory)cfg).factories.length;
        }
        return 1;
    }
}
