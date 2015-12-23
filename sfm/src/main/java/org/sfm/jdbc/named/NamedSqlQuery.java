package org.sfm.jdbc.named;

import org.sfm.utils.Asserts;

import java.util.ArrayList;
import java.util.List;

public class NamedSqlQuery implements ParameterizedQuery {


    private final String sql;
    private final NamedParameter[] parameters;


    private NamedSqlQuery(String sql, NamedParameter[] parameters) {
        this.sql = Asserts.requireNonNull("sql", sql);
        this.parameters = Asserts.requireNonNull("parameters", parameters);
    }


    public static NamedSqlQuery parse(final CharSequence charSequence) {
        Asserts.requireNonNull("charSequence", charSequence);

        final List<NamedParameter> sqlParameters = new ArrayList<NamedParameter>();

        new NamedSqlQueryParser(new NamedSqlQueryParser.Callback() {
            @Override
            public void param(NamedParameter namedParameter) {
                sqlParameters.add(namedParameter);
            }
        }).parse(charSequence);

        return new NamedSqlQuery(
                charSequence.toString(),
                sqlParameters.toArray(new NamedParameter[sqlParameters.size()]));
    }

    public String toSqlQuery() {
        StringBuilder sb = new StringBuilder(sql.length());

        int start = 0;

        for(NamedParameter sqlParameter : parameters) {
            sb.append(sql, start, sqlParameter.getPosition().getStart());
            sb.append("?");
            start = sqlParameter.getPosition().getEnd();
        }

        sb.append(sql, start, sql.length());

        return sb.toString();
    }

    public int getParametersSize() {
        return parameters.length;
    }

    public NamedParameter getParameter(int i) {
        return parameters[i];
    }

}
