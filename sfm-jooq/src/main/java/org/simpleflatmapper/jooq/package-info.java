/**
 * {@link org.simpleflatmapper.jooq.SfmRecordMapperProvider} to integrate in @see <a href="http://www.jooq.org/">jOOQ</a>
 * <p>
 * <code>
 * DSLContext dsl = DSL<br>
 * &nbsp;&nbsp;.using(new DefaultConfiguration()<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;.set(dataSource)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;.set(new SfmRecordMapperProvider()));<br>
 * <br>
 * ...<br>
 * <br>
 * List&lt;MyObject&gt; list = dsl.select().from("TEST_DB_OBJECT").fetchInto(DbObject.class);
 * </code>
 * </p>
 */
package org.simpleflatmapper.jooq;