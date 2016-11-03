module org.simpleflatmapper.sql2o {
        requires public org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires public sql2o;
        requires java.sql;
        exports org.simpleflatmapper.sql2o;
}