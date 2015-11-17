package org.sfm.jdbc.spring;

import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import java.time.LocalDateTime;
import static org.junit.Assert.assertEquals;

public class SfmSqlParameterSourceTest {

    private static final String ID = "id";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";
    private static final String OBSOLETE = "obsolete";
    private static final String SQL = "INSERT into BLAH (ID, CREATE_TIME, UPDATE_TIME, OBSOLETE) VALUES (%s, %s, %s, %s)";
    private static final String PLACEHOLDER_SQL = String.format(SQL, ":" + ID, ":" + CREATE_TIME, ":" + UPDATE_TIME, ":" + OBSOLETE);

    final SfmSqlParameterSourceBuilder<Asset> sfmSqlParameterSourceBuilder = SfmSqlParameterSourceBuilder.newParameterSourceBuilder(
            Asset.class,
            PLACEHOLDER_SQL);

    @Test
    public void shouldMapTpParameterSource() throws Exception {
        final Asset source = new Asset(1L, LocalDateTime.now(), LocalDateTime.now(), false);

        final SqlParameterSource target = sfmSqlParameterSourceBuilder.with(source);

        assertEquals(source.getId(), target.getValue(ID));
        assertEquals(source.getCreateTime(), target.getValue(CREATE_TIME));
        assertEquals(source.getUpdateTime(), target.getValue(UPDATE_TIME));
        assertEquals(source.isObsolete(), target.getValue(OBSOLETE));

        assertEquals(String.format(SQL, "?", "?", "?", "?"),
                     NamedParameterUtils.substituteNamedParameters(PLACEHOLDER_SQL, target));
    }
}