package org.simpleflatmapper.jooq;

import org.simpleflatmapper.jdbc.ResultSetGetterFactory;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.mapper.AbstractColumnNameDiscriminatorMapperFactory;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.property.SpeculativeObjectLookUpProperty;
import org.simpleflatmapper.util.ConstantPredicate;

import java.lang.reflect.Type;
import java.sql.ResultSet;

import static org.simpleflatmapper.jooq.JooqJdbcMapperBuilder.toJdbcColumnKey;

public class SelectQueryMapperFactory
        extends AbstractColumnNameDiscriminatorMapperFactory<JooqFieldKey, SelectQueryMapperFactory, ResultSet> {

    public static SelectQueryMapperFactory newInstance() {
        return new SelectQueryMapperFactory();
    }

    public static SelectQueryMapperFactory newInstance(
            AbstractMapperFactory<JooqFieldKey, ?, ResultSet> config) {
        return new SelectQueryMapperFactory(config);
    }


    private SelectQueryMapperFactory() {
        super(new FieldMapperColumnDefinitionProviderImpl<JooqFieldKey>(), FieldMapperColumnDefinition.<JooqFieldKey>identity(), adapt(ResultSetGetterFactory.INSTANCE));
        addColumnProperty(ConstantPredicate.truePredicate(), SpeculativeObjectLookUpProperty.INSTANCE);
    }



    private SelectQueryMapperFactory(AbstractMapperFactory<JooqFieldKey, ?, ResultSet> config) {
        super(config);
        addColumnProperty(ConstantPredicate.truePredicate(), SpeculativeObjectLookUpProperty.INSTANCE);
    }

    private SelectQueryMapperFactory(AbstractColumnDefinitionProvider<JooqFieldKey> columnDefinitions, FieldMapperColumnDefinition<JooqFieldKey> identity) {
        super(columnDefinitions, identity, adapt(ResultSetGetterFactory.INSTANCE));
        addColumnProperty(ConstantPredicate.truePredicate(), SpeculativeObjectLookUpProperty.INSTANCE);
    }

    private static ContextualGetterFactory<ResultSet, JooqFieldKey> adapt(final ResultSetGetterFactory instance) {
        return new ContextualGetterFactory<ResultSet, JooqFieldKey>() {
            @Override
            public <P> ContextualGetter<ResultSet, P> newGetter(Type target, JooqFieldKey key, MappingContextFactoryBuilder<?, ? extends FieldKey<?>> mappingContextFactoryBuilder, Object... properties) {
                return instance.newGetter(target, toJdbcColumnKey(key), mappingContextFactoryBuilder, properties);
            }
        };
    }

    public <T> SelectQueryMapper<T> newMapper(Class<T> clazz) {
        return new SelectQueryMapper<T>(getReflectionService().getClassMeta(clazz), mapperConfig(clazz));
    }
}
