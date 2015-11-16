package org.sfm.jdbc.spring;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Created by aroger on 16/11/2015.
 */
public interface SqlParameters<T> {
    PlaceHolder<T> getParameter(String column);

    SqlParameterSource value(T value);
}
