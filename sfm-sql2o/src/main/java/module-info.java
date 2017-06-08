module org.simpleflatmapper.sql2o {
        requires transitive org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires transitive sql2o;
        requires java.sql;
        exports org.simpleflatmapper.sql2o;
}