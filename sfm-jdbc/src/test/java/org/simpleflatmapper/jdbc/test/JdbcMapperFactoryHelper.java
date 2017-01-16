package org.simpleflatmapper.jdbc.test;


import org.simpleflatmapper.jdbc.JdbcMapperFactory;

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
