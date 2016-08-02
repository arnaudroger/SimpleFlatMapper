package org.simpleflatmapper.querydsl;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.DateTimePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.StringPath;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QTestDbObject is a Querydsl query type for QTestDbObject
 */
public class QTestDbObject extends com.mysema.query.sql.RelationalPathBase<QTestDbObject> {

    private static final long serialVersionUID = 767356144;

    public static final QTestDbObject testDbObject = new QTestDbObject("TEST_DB_OBJECT");

    public final DateTimePath<java.sql.Timestamp> creationTime = createDateTime("CREATION_TIME", java.sql.Timestamp.class);

    public final StringPath email = createString("EMAIL");

    public final NumberPath<Long> id = createNumber("ID", Long.class);

    public final StringPath name = createString("NAME");

    public final StringPath typeName = createString("TYPE_NAME");

    public final NumberPath<Integer> typeOrdinal = createNumber("TYPE_ORDINAL", Integer.class);

    public final com.mysema.query.sql.PrimaryKey<QTestDbObject> sysPk10093 = createPrimaryKey(id);

    public QTestDbObject(String variable) {
        super(QTestDbObject.class, forVariable(variable), "PUBLIC", "TEST_DB_OBJECT");
    }

    public QTestDbObject(Path<? extends QTestDbObject> entity) {
        super(entity.getType(), entity.getMetadata(), "PUBLIC", "TEST_DB_OBJECT");
    }

    public QTestDbObject(PathMetadata<?> metadata) {
        super(QTestDbObject.class, metadata, "PUBLIC", "TEST_DB_OBJECT");
    }

}

