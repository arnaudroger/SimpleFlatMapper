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
