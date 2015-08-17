package org.simpleflatmapper.batoo;

import org.simpleflatmapper.db.ConnectionParam;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.SQLException;

/**
 * Created by aroger on 17/08/2015.
 */
public class ConnectionParamWithJndi extends ConnectionParam {


    @Override
    public void init() throws SQLException, NamingException {
        super.init();

        // Create initial context
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                org.apache.naming.java.javaURLContextFactory.class.getName());
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        try {
            ic.bind("java:datasource", dataSource);
        } catch(Exception e) {};

    }
}
