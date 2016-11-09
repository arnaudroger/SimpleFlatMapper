[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-poi.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-poi)
[![JavaDoc](https://img.shields.io/badge/javadoc-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-poi)

[Getting Started](http://simpleflatmapper.org/0105-getting-started-poi.html)

# POI Excel Integration

## Quick start

```java
    final SheetMapper<DbObject> parameterGetterMap =
            SheetMapperFactory
                    .newInstance()
                    .newMapper(DbObject.class);

    ...

        try (InputStream is = new FileInputStream("file.xls");
                Workbook workbook = new HSSFWorkbook(is)){
            parameterGetterMap.stream(workbook.getSheetAt(0)).forEach(System.out::println);
        }
```
