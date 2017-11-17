module org.simpleflatmapper.datastax.test {
        requires org.simpleflatmapper.datastax;
        requires org.simpleflatmapper.tuple;
        requires org.simpleflatmapper.reflect;
        requires org.simpleflatmapper.converter;
        requires org.simpleflatmapper.converter.joda;

        requires cassandra.driver.mapping;

        requires junit;
        requires sfm.test;
        requires org.mockito;
        requires joda.time;

        requires org.simpleflatmapper.map.test;
 }