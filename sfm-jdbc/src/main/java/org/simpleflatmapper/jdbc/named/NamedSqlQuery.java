package org.simpleflatmapper.jdbc.named;

import org.simpleflatmapper.jdbc.SizeSupplier;
import org.simpleflatmapper.util.Asserts;

import java.util.ArrayList;
import java.util.List;

public class NamedSqlQuery implements ParameterizedQuery {


    private static final SizeSupplier DEFAULT_SIZE_SUPPLIER = new SizeSupplier() {
        @Override
        public int getSize(int columnIndex) {
            return 1;
        }
    };

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
                sqlParameters.toArray(new NamedParameter[0]));
    }


    public String toSqlQuery() {
        return toSqlQuery(DEFAULT_SIZE_SUPPLIER);
    }

    public String toSqlQuery(SizeSupplier sizeSupplier) {
        StringBuilder sb = new StringBuilder(sql.length());

        int start = 0;

        for(int i = 0; i < parameters.length; i++) {
            NamedParameter sqlParameter = parameters[i];

            sb.append(sql, start, sqlParameter.getPosition().getStart());
            appendParam(sizeSupplier, sb, i);

            start = sqlParameter.getPosition().getEnd();
        }

        sb.append(sql, start, sql.length());

        return sb.toString();
    }

    public void appendParam(SizeSupplier sizeSupplier, StringBuilder sb, int index) {
        int size = sizeSupplier.getSize(index);

        for(int i = 0; i < size; i++) {
            if (i != 0) sb.append(", ");
            sb.append("?");
        }
    }

    public int getParametersSize() {
        return parameters.length;
    }

    public NamedParameter getParameter(int i) {
        return parameters[i];
    }

}
