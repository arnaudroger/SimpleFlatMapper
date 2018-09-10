package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextFactory;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class KeyTupleQueryPreparer<T>  {

    private final ContextFactory contextFactory;
    private final MultiIndexFieldMapper<T>[] multiIndexFieldMappers;
    private final String[] keys;

    public KeyTupleQueryPreparer(MultiIndexFieldMapper<T>[] multiIndexFieldMappers, ContextFactory contextFactory, String[] keys) {
        this.contextFactory = contextFactory;
        if (keys.length != multiIndexFieldMappers.length) {
            throw new IllegalArgumentException("mappers and keys don't match");
        }

        this.multiIndexFieldMappers = multiIndexFieldMappers;
        this.keys = keys;
    }

    public PreparedStatement prepareStatement(CharSequence sqlBase, Connection connection, int size) throws SQLException {
        StringBuilder sb = new StringBuilder(sqlBase);

        if (keys.length == 1) {
            appendSingleSelectIn(keys[0], sb, size);
        } else {
            appendSelectIn(keys, sb, size);
        }

        return connection.prepareStatement(sb.toString());
    }

    public void bindTo(Collection<T> values, PreparedStatement ps, int offset) {

        int index = offset;
        Context context = contextFactory.newContext();
        for(T value : values) {
            for(MultiIndexFieldMapper<T> mapper : multiIndexFieldMappers) {
                try {
                    mapper.map(ps, value, index, context);
                } catch (Exception e) {
                    ErrorHelper.rethrow(e);
                }
                index++;
            }

        }
    }

    private void appendSingleSelectIn(String key, StringBuilder sb, int size) {
        sb.append(" ").append(key).append(" in (");
        for(int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("?");
        }
        sb.append(")");
    }

    private void appendSelectIn(String[] keys, StringBuilder sb, int size) {
        sb.append(" (");
        for(int i = 0; i < size; i++) {

            if (i > 0) {
                sb.append(" or ");
            }
            sb.append("(");
            for(int j = 0; j < keys.length; j++) {
                if (j > 0) {
                    sb.append(" and ");
                }
                sb.append(keys[j]).append(" = ?");
            }
            sb.append(")");
        }
        sb.append(")");
    }

}
