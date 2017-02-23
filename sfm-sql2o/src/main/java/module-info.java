module simpleflatmapper.sql2o {
        requires transitive simpleflatmapper.map;
        requires simpleflatmapper.jdbc;
        requires transitive sql2o;
        requires java.sql;
        exports org.simpleflatmapper.sql2o;
}