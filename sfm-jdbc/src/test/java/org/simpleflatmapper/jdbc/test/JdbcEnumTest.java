package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.property.JdbcGetterFactoryProperty;
import org.simpleflatmapper.map.property.GetterFactoryProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class JdbcEnumTest {


    @Test
    public void mapCustomEmum() throws SQLException {
        Connection conn = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);

        if (conn == null) return;

        try {
            DynamicJdbcMapper<EventLog> mapper =
                    JdbcMapperFactory
                            .newInstance()
                            .addColumnProperty(ConstantPredicate.truePredicate(),
                                    JdbcGetterFactoryProperty.forType(TypeEnum.class, new JdbcGetterFactoryProperty.ResultSetGetter<TypeEnum>() {
                                @Override
                                public TypeEnum get(ResultSet rs, int index) throws SQLException {
                                    return TypeEnum.of(rs.getInt(index));
                                }
                            }))
                            .newMapper(EventLog.class);

            Statement st = conn.createStatement();
            try {
                ResultSet rs = st.executeQuery("select 1 as id, 8 as type");

                rs.next();

                EventLog eventLog = mapper.map(rs);

                assertEquals(1, eventLog.id);
                assertEquals(TypeEnum.DATA_NOT_FOUND, eventLog.type);

            } finally {
                st.close();
            }
        } finally {
            conn.close();
        }
    }


    public enum TypeEnum {
        INFO(0),
        INVALID_API_ARGUMENT(7),
        DATA_NOT_FOUND(8),
        ERROR(9);
        int typeCode;

        TypeEnum(int typeCode) {
            this.typeCode = typeCode;
        }

        public int getTypeCode() {
            return typeCode;
        }

        public static TypeEnum of(int i) {
            switch (i) {
                case 0: return INFO;
                case 7: return INVALID_API_ARGUMENT;
                case 8 : return DATA_NOT_FOUND;
                case 9 : return ERROR;
                default: throw new IllegalArgumentException();
            }
        }
    }


    public static class EventLog {
        private long id;
        private TypeEnum type;

        public EventLog(TypeEnum type) {
            this.type = type;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public TypeEnum getType() {
            return type;
        }

    }
}
