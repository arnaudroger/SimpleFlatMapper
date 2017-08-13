package org.simpleflatmapper.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.types.UInteger;
import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.Consumer;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class Issue453Test {


    public class User implements Serializable {
        public UInteger id;

        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    '}';
        }
    }
    

    @Test
    public void testS() throws SQLException {
        Connection conn = DbHelper.getDbConnection(DbHelper.TargetDB.MYSQL);
        
        if (conn == null) {
            System.out.println("No mysql db");
            return;
        }

        try {
            Statement st = conn.createStatement();
            try {
                st.execute("CREATE TABLE IF NOT EXISTS issue453 ( id int unsigned)");
                st.execute("TRUNCATE TABLE  issue453");
                st.execute("INSERT INTO issue453 values(1), (null)");

                ResultSet resultSet = st.executeQuery("SELECT * FROM issue453");
                
                try {
                    DynamicJdbcMapper<User> userDynamicJdbcMapper = JdbcMapperFactory.newInstance().newMapper(User.class);

                    userDynamicJdbcMapper.forEach(resultSet, new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            System.out.println(user.toString());
                        }
                    });
                } finally {
                    resultSet.close();
                }

            } finally {
                st.close();
            }
            
            
        } finally {
            conn.close();
        }
        

    }

}
