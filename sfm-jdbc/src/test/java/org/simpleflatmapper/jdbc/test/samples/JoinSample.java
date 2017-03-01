package org.simpleflatmapper.jdbc.test.samples;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.util.ListCollector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinSample {


    public void selectUsers() {
        JdbcMapper<Object> jdbcMapper =
                JdbcMapperFactory
                    .newInstance()
                    .addKeys("id", "roles_id", "phones_id")
                    .newMapper(Object.class);
    }


    /**
     * Test for https://arnaudroger.github.io/blog/2017/02/24/jooq-one-to-many.html
     *
     * To avoid having to mock the result set metadata I used a static mapper here. In production code
     * you can just call
     *  newMapper(Location.class)
     * instead of newBuilder()...mapper()
     *
     * @throws SQLException
     */
    @Test
    public void stackOverFlowJoin() throws SQLException {
        JdbcMapper<Location> mapper = JdbcMapperFactory
                .newInstance()
                .addKeys("player")
                .newBuilder(Location.class)
                .addMapping(new JdbcColumnKey("name", 1, Types.VARCHAR))
                .addMapping(new JdbcColumnKey("player", 2, Types.VARCHAR))
                .addMapping(new JdbcColumnKey("invited_players_player", 3, Types.VARCHAR))
                .mapper();

        UUID[] players = new UUID[] { UUID.randomUUID(), UUID.randomUUID()};
        UUID[] invitedPlayers = new UUID[] { UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()};
        String[] name = new String[] { "location1", "location2"};

        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, true, true, false);
        when(rs.getString(1)).thenReturn(name[0], name[1]);
        when(rs.getString(2)).thenReturn(players[0].toString(), players[1].toString());
        when(rs.getObject(2)).thenReturn(players[0].toString(),players[0].toString(),players[0].toString(), players[0].toString(), players[1].toString(), players[1].toString());
        when(rs.getString(3)).thenReturn(invitedPlayers[0].toString(), invitedPlayers[1].toString(), invitedPlayers[2].toString());

        List<Location> list = mapper.forEach(rs, new ListCollector<Location>()).getList();

        assertEquals(2, list.size());

        assertEquals("location1", list.get(0).getName());
        assertEquals(players[0], list.get(0).getPlayer());
        assertEquals(Arrays.asList(invitedPlayers[0], invitedPlayers[1]), list.get(0).getInvitedPlayers());

        assertEquals("location2", list.get(1).getName());
        assertEquals(players[1], list.get(1).getPlayer());
        assertEquals(Arrays.asList(invitedPlayers[2]), list.get(1).getInvitedPlayers());
    }

    public static class Location {
        private final String name;
        private final UUID player;
        private final List<UUID> invitedPlayers;

        public Location(String name, UUID player, List<UUID> invitedPlayers) {
            this.name = name;
            this.player = player;
            this.invitedPlayers = invitedPlayers;
        }

        public String getName() {
            return name;
        }

        public UUID getPlayer() {
            return player;
        }

        public List<UUID> getInvitedPlayers() {
            return invitedPlayers;
        }
    }
}
