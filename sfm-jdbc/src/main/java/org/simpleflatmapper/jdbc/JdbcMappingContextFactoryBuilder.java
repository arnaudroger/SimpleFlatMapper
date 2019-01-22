package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.jdbc.impl.JdbcKeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;

import java.sql.ResultSet;

public class JdbcMappingContextFactoryBuilder extends MappingContextFactoryBuilder<ResultSet, JdbcColumnKey> {
    public JdbcMappingContextFactoryBuilder(boolean ignoreRootKey) {
        super(JdbcKeySourceGetter.INSTANCE, ignoreRootKey);
    }

}
