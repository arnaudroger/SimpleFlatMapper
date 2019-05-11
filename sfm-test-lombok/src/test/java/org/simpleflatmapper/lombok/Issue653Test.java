package org.simpleflatmapper.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Issue653Test {



    @Test
    public void test() throws SQLException {
        JdbcMapper<UserDTO> mapper = JdbcMapperFactory.newInstance()
                .fieldMapperErrorHandler((key, source, target, error, context) -> {
                    System.out.println("Error ! on " + key + " " + error);
                })
                .rowHandlerErrorHandler((t, target) -> System.out.println("Error ! " + t))
//                .addKeys("id", "groups_id", "profile_id", "company_id", "cars_id", "departments_id")
                //.addKeys("id", "groups_id", "profile_id", "company_id")
                .newMapper(UserDTO.class);

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();

            //id | first_name | last_name | username |
            // email | external_id | chat_id | chat_team_id | type | profile_id | profile_phone |
            // profile_avatar | profile_privacy | company_id | company_name | company_description | company_floor |
            // company_phone | company_external_id |
            // groups_id | groups_name
            ResultSet rs = s.executeQuery("select " +
                    "19 as id, 'FirstName' as first_name, 'LastName' as last_name,   'florin1' as username, " +
                    " 'florin1@hotmail.com' as email,  '14616e7d-1e11-4972-b8d1-9567c4fe686a' as external_id,  null as chat_id,  null as chat_team_id,  30 as type,   425 as profile_id,  '730313123' as profile_phone," +
                    "  'url_here' as profile_avatar,   20 as profile_privacy, " +
                    "  8 as company_id,  'Company1' as company_name,  null as company_description,  0  as company_floor, " +
                    " null  as company_phone, null as company_external_id, " +
                    "426  as groups_id,  'Test Top-Managemnet' as groups_name" +
                    " union " +
                    " select " +
                    "19 as id, 'FirstName' as first_name, 'LastName' as last_name,   'florin1' as username, " +
                    " 'florin1@hotmail.com' as email,  '14616e7d-1e11-4972-b8d1-9567c4fe686a' as external_id,  null as chat_id,  null as chat_team_id,  30 as type,   425 as profile_id,  '730313123' as profile_phone," +
                    "  'url_here' as profile_avatar,   20 as profile_privacy, " +
                    "  8 as company_id,  'Company1' as company_name,  null as company_description,  0  as company_floor, " +
                    " null  as company_phone, null as company_external_id, " +
                    "427  as groups_id,  'Primary' as groups_name");

            // use to fail
            mapper.forEach(rs, new CheckedConsumer<UserDTO>() {
                @Override
                public void accept(UserDTO x) throws Exception {
                    System.out.println(x);
                }
            });
        } finally {
            c.close();
        }

    }




}
