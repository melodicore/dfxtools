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

import kotlin.test.Test
import kotlin.test.assertEquals
import me.datafox.dfxtools.handles.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows

/**
 * Tests for [HandleMap].
 *
 * @author Lauri "datafox" Heino
 */
class HandleMapTest {
    lateinit var space: Space

    @BeforeEach
    fun setup() {
        space = HandleManager.createSpace("test space")
    }

    @OptIn(Purge::class)
    @AfterEach
    fun cleanup() {
        HandleManager.purge()
    }

    @Test
    fun `handle set test`() {
        val map = HandleMap<String>(space)
        val handle1 = space.createHandle("test handle 1")
        val handle2 = space.createHandle("test handle 2")
        val handle3 = space.createHandle("test handle 3")
        map.put(handle2, "second")
        map.put(handle3, "third")
        map.put(handle1, "first")
        val it = map.iterator()
        assertEquals("first", it.next().value)
        assertEquals("second", it.next().value)
        assertEquals("third", it.next().value)
        assertThrows<IllegalArgumentException> { map.put(space.handle, "error") }
        map.remove(handle2)
        assertEquals(listOf("first", "third"), map.values.toList())
    }

    @Test
    fun `handle map tag test`() {
        val map = HandleMap<String>(space)
        val handle1 = space.createHandle("test handle 1")
        val handle2 = space.createHandle("test handle 2")
        val handle3 = space.createHandle("test handle 3")
        map.put(handle1, "first")
        map.put(handle2, "second")
        map.put(handle3, "third")
        val tag1 = HandleManager.tagSpace.createHandle("test tag 1")
        val tag2 = HandleManager.tagSpace.createHandle("test tag 2")
        val tag3 = HandleManager.tagSpace.createHandle("test tag 3")
        handle1 += listOf(tag1, tag2, tag3)
        handle2 += listOf(tag2, tag3)
        handle3 += tag3
        assertEquals(listOf("first", "second", "third"), map.getByTag(tag3))
        assertEquals(listOf("first", "second"), map.getByTag("test tag 2"))
        handle3 += tag1
        assertEquals(listOf("first", "third"), map.getByTags(setOf(tag1, tag3)))
        assertEquals(listOf("first"), map.getByTags(setOf("test tag 1", "test tag 2")))
    }
}
