[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-jooq.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-jooq)
[![JavaDoc](https://img.shields.io/badge/javadoc-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-jooq)

# jOOQ integration

[Getting Started](http://simpleflatmapper.org/0106-getting-started-jooq.html)

## SFM as a RecordMapperProvider

Sfm can be plugged into the fetchInto mapping.


```java
configuration.set(new SfmRecordMapperProvider()));
```


See [SFM Integration with jOOQ faster object mapping](https://github.com/arnaudroger/SimpleFlatMapper/wiki/SFM-Integration-with-Jooq-faster-object-mapping)
for more options and their performance implications.
