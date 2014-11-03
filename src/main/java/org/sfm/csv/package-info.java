/**
 * {@link org.sfm.csv.CsvMapper} classes to map object from a csv file. It is instantiated using the {@link org.sfm.csv.CsvMapperFactory} 
 * <blockquote>
 * CsvMapper<MyObject> mapper = CsvMapperFactory.newInstance().newMapper(MyObject.class);<br />
 * mapper.forEach(reader, (o) -> writer.append(o.toString()).append("\n"));
 * </blockquote>
 */
package org.sfm.csv;