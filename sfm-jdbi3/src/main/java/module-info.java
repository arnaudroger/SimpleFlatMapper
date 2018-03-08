module org.simpleflatmapper.jdbi3 {
        requires transitive org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires transitive jdbi3.core;
        requires transitive java.sql;

        exports org.simpleflatmapper.jdbi3;

        provides org.jdbi.v3.core.spi.JdbiPlugin
                with org.simpleflatmapper.jdbi3.SfmJdbiPlugin;
}