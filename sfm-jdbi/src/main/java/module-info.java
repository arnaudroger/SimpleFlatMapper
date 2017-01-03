module org.simpleflatmapper.jdbi {
        requires public org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires public jdbi;
        requires public java.sql;

        exports org.simpleflatmapper.jdbi;
}