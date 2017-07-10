package org.simpleflatmapper.datastax.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.datastax.DatastaxCrud;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.junit.LibrarySets;
import org.simpleflatmapper.test.junit.MultiClassLoaderJunitRunner;

//IFJAVA8_START
@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySets(
        librarySets = {
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.1.0/cassandra-driver-core-3.1.0.jar",
        },
        includes={Converter.class, ReflectionService.class, Mapper.class, DatastaxCrud.class, DatastaxCrudTest.class},
        excludes = { "org.junit", "io.netty"},
        names={"v303"}
)
@Suite.SuiteClasses({
    Datastax3.class,
    DatastaxCrudTest.class,
    DatastaxMapperCollectionTest.class,
    DatastaxMapperFactoryTest.class,
    DatastaxMapperTupleTest.class,
    DatastaxMapperUDTTest.class,
    DatastaxNumberTest.class,
    DataTypeTest.class,
    SettableDataMapperTest.class
})
//IFJAVA8_END
public class Datastax3SuiteTest {
}
