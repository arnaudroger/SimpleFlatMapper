//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import org.simpleflatmapper.map.annotation.Key;

public class CompanyDTO {
    @Key
    private Long id;
    private String name;
    private String externalId;
    private int floor;
    private String phone;
    private String description;

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public int getFloor() {
        return this.floor;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getDescription() {
        return this.description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "CompanyDTO(id=" + var10000 + ", name=" + this.getName() + ", externalId=" + this.getExternalId() + ", floor=" + this.getFloor() + ", phone=" + this.getPhone() + ", description=" + this.getDescription() + ")";
    }

    public CompanyDTO() {
    }

    public CompanyDTO(Long id, String name, String externalId, int floor, String phone, String description) {
        this.id = id;
        this.name = name;
        this.externalId = externalId;
        this.floor = floor;
        this.phone = phone;
        this.description = description;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CompanyDTO)) {
            return false;
        } else {
            CompanyDTO other = (CompanyDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label75: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label75;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label75;
                    }

                    return false;
                }

                Object this$name = this.getName();
                Object other$name = other.getName();
                if (this$name == null) {
                    if (other$name != null) {
                        return false;
                    }
                } else if (!this$name.equals(other$name)) {
                    return false;
                }

                Object this$externalId = this.getExternalId();
                Object other$externalId = other.getExternalId();
                if (this$externalId == null) {
                    if (other$externalId != null) {
                        return false;
                    }
                } else if (!this$externalId.equals(other$externalId)) {
                    return false;
                }

                if (this.getFloor() != other.getFloor()) {
                    return false;
                } else {
                    Object this$phone = this.getPhone();
                    Object other$phone = other.getPhone();
                    if (this$phone == null) {
                        if (other$phone != null) {
                            return false;
                        }
                    } else if (!this$phone.equals(other$phone)) {
                        return false;
                    }

                    Object this$description = this.getDescription();
                    Object other$description = other.getDescription();
                    if (this$description == null) {
                        if (other$description != null) {
                            return false;
                        }
                    } else if (!this$description.equals(other$description)) {
                        return false;
                    }

                    return true;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof CompanyDTO;
    }

    public int hashCode() {
        int PRIME = true;
        int result = 1;
        Object $id = this.getId();
        int result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $externalId = this.getExternalId();
        result = result * 59 + ($externalId == null ? 43 : $externalId.hashCode());
        result = result * 59 + this.getFloor();
        Object $phone = this.getPhone();
        result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        return result;
    }
}
