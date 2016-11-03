module org.simpleflatmapper.jooq {
        requires public org.simpleflatmapper.map;
        requires org.simpleflatmapper.jdbc;
        requires public jooq;
        requires java.sql;
        exports org.simpleflatmapper.jooq;
}