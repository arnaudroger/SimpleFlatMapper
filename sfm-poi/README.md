[![Maven Central](https://img.shields.io/maven-central/v/org.simpleflatmapper/sfm-poi.svg)](https://maven-badges.herokuapp.com/maven-central/org.simpleflatmapper/sfm-poi)
[![JavaDoc](https://img.shields.io/badge/javadoc-2.0-blue.svg)](http://www.javadoc.io/doc/org.simpleflatmapper/sfm-poi)

# POI Excel Integration

## Add dependency

```xml
		<dependency>
			<groupId>org.simpleflatmapper</groupId>
			<artifactId>sfm-poi</artifactId>
			<version>2.0</version>
		</dependency>
```
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
