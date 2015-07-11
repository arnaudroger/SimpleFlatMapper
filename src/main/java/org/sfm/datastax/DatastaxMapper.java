package org.sfm.datastax;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.map.EnumarableMapper;
import org.sfm.map.Mapper;


/**
 *
 *
 */
public interface DatastaxMapper<T> extends Mapper<Row, T>, EnumarableMapper<ResultSet, T, DriverException> {

}
