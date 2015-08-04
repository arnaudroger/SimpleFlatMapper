package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.map.SetRowMapper;


/**
 *
 *
 */
public interface DatastaxMapper<T> extends SetRowMapper<Row, ResultSet, T, DriverException> {

}
