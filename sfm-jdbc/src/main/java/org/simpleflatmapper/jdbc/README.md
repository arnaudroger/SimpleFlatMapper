JdbcMapper
---------

```java

public class MyDao {
    private final JdbcMapper<MyObject> parameterGetterMap =
        JdbcMapperFactory.newInstance().newMapper(MyObject.class);
    public void printAllLambda(Writer writer, Connection conn) throws SQLException {
        try (PreparedStatement ps =
                conn.prepareStatement("select id, email, my_property from MyTable");
                ResultSet rs = ps.executeQuery()) {
            parameterGetterMap.forEach(rs, (o) -> writer.append(o.toString()).append("\n"));
        }
    }
    public void printAll(Writer writer, Connection conn) throws SQLException {
        try (PreparedStatement ps =
                conn.prepareStatement("select id, email, my_property from MyTable");
                ResultSet rs = ps.executeQuery()) {
            parameterGetterMap.forEach(rs, new CheckedConsumer<MyObject>{
                public void handle(MyObject o) throws IOException {
                    writer.append(o.toString()).append("\n");
                }
            });
        }
    }
}
```


