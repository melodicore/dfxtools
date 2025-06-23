
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.entities.Entity
import me.datafox.dfxtools.entities.Schema
import me.datafox.dfxtools.handles.putHandled
import kotlin.test.Test

/**
 * @author Lauri "datafox" Heino
 */
class Testes {
    @Test
    fun `test test`() {
        Engine.schemas.putHandled(Schema("schema1",
            mapOf(Pair(String::class, setOf("data1")))))
        val entity = Entity("entity1")
        val c = entity.createComponent("component")
        c[String::class, "data1"] = "test"
        c[Int::class, "data2"] = 1
        Engine.entities.putHandled(entity)
        println(c.data)
        println(c.schemas)
        Engine.schemas.putHandled(Schema("schema2",
            mapOf(Pair(Int::class, setOf("data2")))))
        println(c.data)
        println(c.schemas)
        println(Engine.save(true))
    }
}