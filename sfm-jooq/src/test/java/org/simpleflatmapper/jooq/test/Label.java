package org.simpleflatmapper.jooq.test;

import java.util.UUID;

public class Label {
    private Integer id;
    private UUID uuid;
    private String name;
    private Boolean obsolete;


    public Label(Integer id, UUID uuid, String name, Boolean obsolete) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.obsolete = obsolete;
    }


    public Integer getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Boolean getObsolete() {
        return obsolete;
    }
}
