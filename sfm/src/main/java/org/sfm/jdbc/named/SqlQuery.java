package org.sfm.jdbc.named;

public class SqlQuery implements ParameterizedQuery {
    private final String query;
    private final NamedParameter[] parameters;


    public SqlQuery(String query, NamedParameter[] parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public int getParametersSize() {
        return parameters.length;
    }

    @Override
    public NamedParameter getParameter(int i) {
        return parameters[i];
    }
}
