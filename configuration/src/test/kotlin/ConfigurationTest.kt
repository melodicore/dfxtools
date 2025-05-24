import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * @author Lauri "datafox" Heino
 */
class ConfigurationTest {
    val string = "default"
    val string2 = "something"
    val int = 42
    val int2 = 419
    val int3 = 68
    val boolean = false
    val boolean2 = true
    val stringKey = ConfigurationKey(string)
    val intKey = ConfigurationKey(int)
    val booleanKey = ConfigurationKey(boolean)

    @Test
    fun `configuration test`() {
        val config = Configuration()
        assertEquals(string, config[stringKey])
        assertEquals(int, config[intKey])
        assertEquals(boolean, config[booleanKey])
        config[stringKey] = { string2 }
        assertEquals(string2, config[stringKey])
        var first = true
        config[intKey] = {
            if(first) {
                first = false
                int2
            } else int3
        }
        assertEquals(int2, config[intKey])
        assertEquals(int3, config[intKey])
        assertEquals(string2, config.remove(stringKey))
        assertNull(config.remove(stringKey))
        assertEquals(string, config[stringKey])
        val config2 = Configuration()
        config2[booleanKey] = { boolean2 }
        config.append(config2)
        assertEquals(string, config[stringKey])
        assertEquals(int3, config[intKey])
        assertEquals(boolean2, config[booleanKey])
        val config3 = Configuration(config)
        assertEquals(string, config3[stringKey])
        assertEquals(int3, config3[intKey])
        assertEquals(boolean2, config3[booleanKey])
        config.clear()
        assertEquals(string, config[stringKey])
        assertEquals(int, config[intKey])
        assertEquals(boolean, config[booleanKey])
    }
}