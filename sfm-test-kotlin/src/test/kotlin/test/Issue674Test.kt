import org.junit.Test
import org.simpleflatmapper.csv.CsvParser
import org.simpleflatmapper.jdbc.JdbcMapperFactory
import org.simpleflatmapper.map.getter.ContextualGetter
import org.simpleflatmapper.tuple.Tuple2
import org.simpleflatmapper.util.TypeReference
import test.CsvLine
import java.sql.ResultSet

import kotlin.test.assertEquals


class Issue674Test {
    @Test
    fun test() {

        val builder = JdbcMapperFactory.newInstance().useAsm(false)
                .addKeys("parentId", "childId")
                .addAlias("parentId", "children.parentId")
                .addAlias("childId", "children.childId")
                .addAlias("propFromResultSetA", "children.propA")
                .addAlias("propFromResultSetB", "children.propB")
                .newBuilder(ParentEntity::class.java)


        builder.addKey("parentId")
        builder.addKey("childId")
        builder.addMapping("propFromResultSetA")
        builder.addMapping("propFromResultSetB")


        val mapper = builder.mapper();


        println("mapper = ${mapper}")
    }
}

data class ParentEntity(
        var parentId : Int,
        var children: MutableList<ChildEntity>
)

data class ChildEntity(
        var parentId: Int,
        var childId: Int,
        var propA: String,
        var propB: String
)

