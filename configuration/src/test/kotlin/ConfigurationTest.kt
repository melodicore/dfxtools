/*
 * Copyright 2025 Lauri "datafox" Heino
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import me.datafox.dfxtools.configuration.Configuration
import me.datafox.dfxtools.configuration.ConfigurationKey
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Just a very simple [`configuration test`] case since the module is tiny and barebones, leveraging [HashMap] for
 * functionality.
 *
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