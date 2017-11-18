module sfm.test {
        exports org.simpleflatmapper.test.beans;
        exports org.simpleflatmapper.test.jdbc;
        exports org.simpleflatmapper.test.junit;
        requires junit;
        requires java.sql;
        requires java.naming;
}