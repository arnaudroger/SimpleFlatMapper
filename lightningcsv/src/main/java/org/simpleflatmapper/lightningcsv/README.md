

Uses header to match to the property.

```java
public class MyParser {
    private final CsvMapper<MyObject> parameterGetterMap =
    	CsvMapperFactory.newInstance().newMapper(MyObject.class);
    public void printAll(Writer writer, Reader reader) throws IOException {
        parameterGetterMap.forEach(reader, (o) -> writer.append(o.toString()).append("\n"));
    }
}
```

Supported type
------

Csv parameterGetterMap can map to :

boolean, byte, char, date, double, enum, float, integer, long short, string

Or any object compose of those. It also can map to list or array.


