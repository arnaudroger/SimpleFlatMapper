[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-poi.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-poi)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-poi)
[![License](https://img.shields.io/github/license/arnaudroger/simpleFlatMapper.svg)](https://raw.githubusercontent.com/arnaudroger/SimpleFlatMapper/master/LICENSE)

[![Java 6](https://img.shields.io/badge/java-6-orange.svg)](#java-6)[![Java 7](https://img.shields.io/badge/java-7-green.svg)](#java-7)[![Java 8](https://img.shields.io/badge/java-8-brightgreen.svg)](#java-8)[![Java 9-b72](https://img.shields.io/badge/java-9-brightgreen.svg)](#java-8)

[![Build Status](https://img.shields.io/travis/arnaudroger/SimpleFlatMapper.svg)](https://travis-ci.org/arnaudroger/SimpleFlatMapper)
[![Coverage Status](https://img.shields.io/coveralls/arnaudroger/SimpleFlatMapper.svg)](https://coveralls.io/r/arnaudroger/SimpleFlatMapper)

# POI Excel Integration

## Quick start

```java
    File file = new File("file.xls");
    SheetMapper<DbObject> mapper =
            SheetMapperFactory
                    .newInstance()
                    .newMapper(DbObject.class);

    try (InputStream is = new FileInputStream(file);
            Workbook workbook = new HSSFWorkbook(is)){
        mapper.stream(workbook.getSheetAt(0)).forEach(System.out::println);
    }
```
