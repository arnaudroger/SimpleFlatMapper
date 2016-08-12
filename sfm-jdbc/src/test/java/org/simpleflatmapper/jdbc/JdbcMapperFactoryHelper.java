package org.simpleflatmapper.jdbc;


public class JdbcMapperFactoryHelper {
    public static JdbcMapperFactory asm() {
        return noFailOnAsm().failOnAsm(true);
    }

    public static JdbcMapperFactory noAsm() {
        return asm().useAsm(false);
    }

    public static JdbcMapperFactory noFailOnAsm() {
        return JdbcMapperFactory.newInstance();
    }
}
