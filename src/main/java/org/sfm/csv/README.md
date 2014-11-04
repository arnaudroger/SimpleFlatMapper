

Uses header to match to the property.

```java
public class MyParser {
    private final CsvMapper<MyObject> mapper = 
    	CsvMapperFactory.newInstance().newMapper(MyObject.class);
    public void printAll(Writer writer, Reader reader) throws IOException {
        mapper.forEach(reader, (o) -> writer.append(o.toString()).append("\n"));
    }
}
```

Supported type
------

Csv mapper can map to :

boolean, byte, char, date, double, enum, float, integer, long short, string

Or any object compose of those. It also can map to list or array.


