package org.sfm.datastax;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.LibrarySets;
import org.sfm.utils.MultiClassLoaderJunitRunner;

//IFJAVA8_START
@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySets(
        librarySets = {
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar"
        },
        includes={ReflectionService.class, DatastaxCrud.class, DatastaxCrudTest.class},
        excludes = { "org.junit", "org.sfm.datastax.DatastaxCrudTest", "io.netty"},
        names={"v303", "v218"}
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
