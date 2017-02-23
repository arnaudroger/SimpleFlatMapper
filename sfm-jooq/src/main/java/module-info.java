module simpleflatmapper.jooq {
        requires transitive simpleflatmapper.map;
        requires simpleflatmapper.jdbc;
        requires transitive jooq;
        requires java.sql;
        exports org.simpleflatmapper.jooq;
}