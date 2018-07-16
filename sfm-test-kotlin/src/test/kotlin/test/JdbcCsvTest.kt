import org.junit.Test
import org.simpleflatmapper.jdbc.JdbcMapperFactory
import org.simpleflatmapper.util.TypeReference
import test.CsvLine
import java.sql.DriverManager

import kotlin.test.assertEquals


class JdbcCsvTest {
    @Test
    fun test() {
        val connection = DriverManager.getConnection("jdbc:h2:mem:");
        
        connection.use { 
            it.createStatement().use { 
                val rs = it.executeQuery("SELECT 1 as elt0,2 as elt1")
                rs.use { 
                    val pair = JdbcMapperFactory
                            .newInstance()
                            .newMapper(object : TypeReference<Pair<Int, Int>>() {})
                            .iterator(rs).next()
                    println(pair)
                    assertEquals(1, pair.first)
                    assertEquals(2, pair.second)
                }
                val rs2 = it.executeQuery("SELECT 1 as elt0,2 as elt1, 3 as elt2")
                rs2.use {
                    val triple = JdbcMapperFactory
                            .newInstance()
                            .newMapper(object : TypeReference<Triple<Int, Int, Int>>() {})
                            .iterator(rs2).next()
                    println(triple)
                    assertEquals(1, triple.first)
                    assertEquals(2, triple.second)
                    assertEquals(3, triple.third)

                }
            }
        }
    }
    
    @Test
    fun test538() {
        val connection = DriverManager.getConnection("jdbc:h2:mem:");

        connection.use {
            it.createStatement().use {
                val rs = it.executeQuery("SELECT 1 as field1,2 as field2")
                rs.use {
                    val item = JdbcMapperFactory
                            .newInstance()
                            .newMapper(CsvLine::class.java)
                            .iterator(rs).next()
                    println(item)
                    assertEquals("1", item.field1)
                    assertEquals("2", item.field2)
                }
            }
        }

    }
}
