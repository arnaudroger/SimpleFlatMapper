module simpleflatmapper.datastax.test {
        requires simpleflatmapper.datastax;
        requires simpleflatmapper.tuple;
        requires simpleflatmapper.reflect;
        requires simpleflatmapper.converter;
        requires simpleflatmapper.converter.joda;

        requires cassandra.driver.mapping;
        requires libthrift;

        requires junit;
        requires sfm.test;
        requires mockito.core;
        requires cassandra.unit;
        requires cassandra.all;
        requires joda.time;

        requires simpleflatmapper.map.test;
 }