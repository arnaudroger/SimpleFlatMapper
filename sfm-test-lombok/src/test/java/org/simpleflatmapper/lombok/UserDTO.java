package org.simpleflatmapper.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.simpleflatmapper.map.annotation.Key;

import java.util.List;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public class UserDTO {

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

    }