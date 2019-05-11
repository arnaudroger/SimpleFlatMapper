package org.simpleflatmapper.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.simpleflatmapper.map.annotation.Key;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CompanyDTO {

    @Key
    private Long id;
    private String name;
    private String externalId;
    private int floor;
    private String phone;
    private String description;

}