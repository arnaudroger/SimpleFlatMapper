/**
 * {@link org.simpleflatmapper.csv.CsvMapper} classes to map object from a csv file. It is instantiated using the {@link org.simpleflatmapper.csv.CsvMapperFactory}
 * <p>
 * <code>
 * CsvMapper&lt;MyObject&gt; jdbcMapper = CsvMapperFactory.newInstance().newMapper(MyObject.class);<br><br>
 * jdbcMapper.forEach(reader, (o) -&gt; writer.append(o.toString()).append("\n"));
 * </code>
 * </p>
 */
package org.simpleflatmapper.csv;