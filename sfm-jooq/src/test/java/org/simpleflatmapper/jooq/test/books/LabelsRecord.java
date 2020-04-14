//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.simpleflatmapper.jooq.test.books;

import java.util.UUID;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

public class LabelsRecord extends UpdatableRecordImpl<LabelsRecord> implements Record4<Integer, UUID, String, Boolean> {
    private static final long serialVersionUID = 1840732564L;

    public void setId(Integer value) {
        this.set(0, value);
    }

    public Integer getId() {
        return (Integer)this.get(0);
    }

    public void setUuid(UUID value) {
        this.set(1, value);
    }

    public UUID getUuid() {
        return (UUID)this.get(1);
    }

    public void setName(String value) {
        this.set(2, value);
    }

    public String getName() {
        return (String)this.get(2);
    }

    public void setObsolete(Boolean value) {
        this.set(3, value);
    }

    public Boolean getObsolete() {
        return (Boolean)this.get(3);
    }


    public Record1<Integer> key() {
        return (Record1)super.key();
    }

    public Row4<Integer, UUID, String, Boolean> fieldsRow() {
        return (Row4)super.fieldsRow();
    }

    public Row4<Integer, UUID, String, Boolean> valuesRow() {
        return (Row4)super.valuesRow();
    }

    public Field<Integer> field1() {
        return Labels.LABELS.ID;
    }

    public Field<UUID> field2() {
        return Labels.LABELS.UUID;
    }

    public Field<String> field3() {
        return Labels.LABELS.NAME;
    }

    public Field<Boolean> field4() {
        return Labels.LABELS.OBSOLETE;
    }


    public Integer component1() {
        return this.getId();
    }

    public UUID component2() {
        return this.getUuid();
    }

    public String component3() {
        return this.getName();
    }

    public Boolean component4() {
        return this.getObsolete();
    }


    public Integer value1() {
        return this.getId();
    }

    public UUID value2() {
        return this.getUuid();
    }

    public String value3() {
        return this.getName();
    }

    public Boolean value4() {
        return this.getObsolete();
    }

    public LabelsRecord value1(Integer value) {
        this.setId(value);
        return this;
    }

    public LabelsRecord value2(UUID value) {
        this.setUuid(value);
        return this;
    }

    public LabelsRecord value3(String value) {
        this.setName(value);
        return this;
    }

    public LabelsRecord value4(Boolean value) {
        this.setObsolete(value);
        return this;
    }


    public LabelsRecord values(Integer value1, UUID value2, String value3, Boolean value4) {
        this.value1(value1);
        this.value2(value2);
        this.value3(value3);
        this.value4(value4);
        return this;
    }

    public LabelsRecord() {
        super(Labels.LABELS);
    }

    public LabelsRecord(Integer id, UUID uuid, String name, Boolean obsolete) {
        super(Labels.LABELS);
        this.set(0, id);
        this.set(1, uuid);
        this.set(2, name);
        this.set(3, obsolete);
    }
}
