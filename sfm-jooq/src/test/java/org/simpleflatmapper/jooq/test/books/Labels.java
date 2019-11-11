//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.simpleflatmapper.jooq.test.books;

import java.util.List;
import java.util.UUID;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

public class Labels extends TableImpl<LabelsRecord> {
    private static final long serialVersionUID = 359479367L;
    public static final Labels LABELS = new Labels();
    public final TableField<LabelsRecord, Integer> ID;
    public final TableField<LabelsRecord, UUID> UUID;
    public final TableField<LabelsRecord, String> NAME;
    public final TableField<LabelsRecord, Boolean> OBSOLETE;

    public Class<LabelsRecord> getRecordType() {
        return LabelsRecord.class;
    }

    public Labels() {
        this((Name)DSL.name("labels"), (Table)null);
    }

    public Labels(String alias) {
        this((Name)DSL.name(alias), (Table)LABELS);
    }

    public Labels(Name alias) {
        this((Name)alias, (Table)LABELS);
    }

    private Labels(Name alias, Table<LabelsRecord> aliased) {
        this(alias, aliased, (Field[])null);
    }

    private Labels(Name alias, Table<LabelsRecord> aliased, Field<?>[] parameters) {
        super(alias, (Schema)null, aliased, parameters, DSL.comment(""));
        this.ID = createField("id", SQLDataType.INTEGER.nullable(false).defaultValue(DSL.field("nextval('tags_id_seq'::regclass)", SQLDataType.INTEGER)), this, "");
        this.UUID = createField("uuid", SQLDataType.UUID.nullable(false), this, "");
        this.NAME = createField("name", SQLDataType.VARCHAR(500).nullable(false), this, "");
        this.OBSOLETE = createField("obsolete", SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");
    }

    public <O extends Record> Labels(Table<O> child, ForeignKey<O, LabelsRecord> key) {
        super(child, key, LABELS);
        this.ID = createField("id", SQLDataType.INTEGER.nullable(false).defaultValue(DSL.field("nextval('tags_id_seq'::regclass)", SQLDataType.INTEGER)), this, "");
        this.UUID = createField("uuid", SQLDataType.UUID.nullable(false), this, "");
        this.NAME = createField("name", SQLDataType.VARCHAR(500).nullable(false), this, "");
        this.OBSOLETE = createField("obsolete", SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");
    }

    public Schema getSchema() {
        return null;
    }

    public Identity<LabelsRecord, Integer> getIdentity() {
        return null;
    }

    public UniqueKey<LabelsRecord> getPrimaryKey() {
        return null;
    }

    public List<UniqueKey<LabelsRecord>> getKeys() {
        return null;
    }

    public Labels as(String alias) {
        return new Labels(DSL.name(alias), this);
    }

    public Labels as(Name alias) {
        return new Labels(alias, this);
    }

    public Labels rename(String name) {
        return new Labels(DSL.name(name), (Table)null);
    }

    public Labels rename(Name name) {
        return new Labels(name, (Table)null);
    }
}
