package org.sfm.jdbc;


public class JdbcMapperFactoryHelper {
    public static JdbcMapperFactory asm() {
        return noFailOnAsm().failOnAsm(true);
    }

    public static JdbcMapperFactory noAsm() {
        return asm().useAsm(false);
    }

    public static JdbcMapperFactory disableAsm() {
        return asm().disableAsm(true);
    }

    public static JdbcMapperFactory noFailOnAsm() {
        return JdbcMapperFactory.newInstance();
    }
}
