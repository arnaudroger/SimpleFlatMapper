package org.simpleflatmapper.jdbi3;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.spi.JdbiPlugin;

public class SfmJdbiPlugin implements JdbiPlugin {
    @Override
    public void customizeJdbi(Jdbi db) {
        db.registerRowMapper(new SfmRowMapperFactory());
    }
}
