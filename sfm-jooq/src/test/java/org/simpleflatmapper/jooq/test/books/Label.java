package org.simpleflatmapper.jooq.test.books;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        if (id != null ? !id.equals(label.id) : label.id != null) return false;
        if (uuid != null ? !uuid.equals(label.uuid) : label.uuid != null) return false;
        if (name != null ? !name.equals(label.name) : label.name != null) return false;
        return obsolete != null ? obsolete.equals(label.obsolete) : label.obsolete == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (obsolete != null ? obsolete.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Label{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", name='" + name + '\'' +
                ", obsolete=" + obsolete +
                '}';
    }
}
