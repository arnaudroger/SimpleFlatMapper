package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.CheckedBiFunction;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
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
            ResultSet rs = s.executeQuery("select 19 as id, 'FirstName' as first_name, 'LastName' as last_name,   'florin1' as username, " +
                    " 'florin1@hotmail.com' as email,  '14616e7d-1e11-4972-b8d1-9567c4fe686a' as external_id,  null as chat_id,  null as chat_team_id,  30 as type,   425 as profile_id,  '730313123' as profile_phone," +
                    "  'url_here' as profile_avatar,   20 as profile_privacy, " +
                    "  8 as company_id,  'Company1' as company_name,  null as company_description,  0  as company_floor, " +
                    " null  as company_phone, null as company_external_id, " +
                    "426  as groups_id,  'Test Top-Managemnet' as groups_name");

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

    public static class UserDTO {

        @Key
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String username;
        private String externalId;
        private String chatId;
        private String chatTeamId;
        private int type;
        private ProfileDTO profile;
        private CompanyDTO company;
        private List<GroupDTO> groups;

        public UserDTO(Long id, String firstName, String lastName, String email, String username, String externalId, String chatId, String chatTeamId, int type, ProfileDTO profile, CompanyDTO company, List<GroupDTO> groups) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.externalId = externalId;
            this.chatId = chatId;
            this.chatTeamId = chatTeamId;
            this.type = type;
            this.profile = profile;
            this.company = company;
            this.groups = groups;
        }

        public Long getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getUsername() {
            return username;
        }

        public String getExternalId() {
            return externalId;
        }

        public String getChatId() {
            return chatId;
        }

        public String getChatTeamId() {
            return chatTeamId;
        }

        public int getType() {
            return type;
        }

        public ProfileDTO getProfile() {
            return profile;
        }

        public CompanyDTO getCompany() {
            return company;
        }

        public List<GroupDTO> getGroups() {
            return groups;
        }

        @Override
        public String toString() {
            return "UserDTO{" +
                    "id=" + id +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", email='" + email + '\'' +
                    ", username='" + username + '\'' +
                    ", externalId='" + externalId + '\'' +
                    ", chatId='" + chatId + '\'' +
                    ", chatTeamId='" + chatTeamId + '\'' +
                    ", type=" + type +
                    ", profile=" + profile +
                    ", company=" + company +
                    ", groups=" + groups +
                    '}';
        }
    }

    public static class ProfileDTO {

        @Key
        private Long id;
        private String phone;
        private String avatar;
        private int privacy;

        public ProfileDTO(Long id, String phone, String avatar, int privacy) {
            this.id = id;
            this.phone = phone;
            this.avatar = avatar;
            this.privacy = privacy;
        }

        public Long getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        public String getAvatar() {
            return avatar;
        }

        public int getPrivacy() {
            return privacy;
        }

        @Override
        public String toString() {
            return "ProfileDTO{" +
                    "id=" + id +
                    ", phone='" + phone + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", privacy=" + privacy +
                    '}';
        }
    }

    public static class CompanyDTO {

        @Key
        private Long id;
        private String name;
        private String externalId;
        private int floor;
        private String phone;
        private String description;

        public CompanyDTO(Long id, String name, String externalId, int floor, String phone, String description) {
            this.id = id;
            this.name = name;
            this.externalId = externalId;
            this.floor = floor;
            this.phone = phone;
            this.description = description;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getExternalId() {
            return externalId;
        }

        public int getFloor() {
            return floor;
        }

        public String getPhone() {
            return phone;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "CompanyDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", externalId='" + externalId + '\'' +
                    ", floor=" + floor +
                    ", phone='" + phone + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public static class GroupDTO {

        @Key
        private Long id;
        private String name;

        public GroupDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "GroupDTO{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


}
