module simpleflatmapper.jdbi3 {
        requires transitive simpleflatmapper.map;
        requires simpleflatmapper.jdbc;
        requires transitive jdbi3;
        requires transitive java.sql;

        exports org.simpleflatmapper.jdbi3;

        provides org.jdbi.v3.core.spi.JdbiPlugin
                with org.simpleflatmapper.jdbi3.SfmJdbiPlugin;
}