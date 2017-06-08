module org.simpleflatmapper.jdbi {
        requires transitive org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires transitive jdbi;
        requires transitive java.sql;

        exports org.simpleflatmapper.jdbi;
}