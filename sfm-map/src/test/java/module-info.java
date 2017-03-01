module simpleflatmapper.map.test {
        requires simpleflatmapper.map;
        requires simpleflatmapper.tuples;
        requires junit;
        requires sfm.test;
        requires mockito.core;
        requires joda.time;


        exports org.simpleflatmapper.test.core.mapper;
 }