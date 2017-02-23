module simpleflatmapper.jdbi {
        requires transitive simpleflatmapper.map;
        requires simpleflatmapper.jdbc;
        requires transitive jdbi;
        requires transitive java.sql;

        exports org.simpleflatmapper.jdbi;
}