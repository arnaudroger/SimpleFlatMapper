package org.simpleflatmapper.jooq.test;

import org.jooq.types.UInteger;
import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.Consumer;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
