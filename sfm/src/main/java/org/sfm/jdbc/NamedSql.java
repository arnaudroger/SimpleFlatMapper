package org.sfm.jdbc;

public class NamedSql {

    private final String sql;
    private final String[] paramNames;


    private NamedSql(String sql, String[] paramNames) {
        this.sql = sql;
        this.paramNames = paramNames;
    }


    public static NamedSql parse(CharSequence charSequence) {

        for(int i = 0; i < charSequence.length(); i++) {
            char c = charSequence.charAt(i);

        }
        return null;
    }
}
