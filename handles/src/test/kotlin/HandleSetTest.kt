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

import me.datafox.dfxtools.handles.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for [HandleSet].
 *
 * @author Lauri "datafox" Heino
 */
class HandleSetTest {
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
        val set = HandleSet(space)
        val handle1 = space.createHandle("test handle 1")
        set += listOf("test handle 2", "test handle 3")
        set += "test handle 1"
        val it = set.iterator()
        assertEquals(handle1, it.next())
        val handle2 = it.next()
        val handle3 = it.next()
        assertEquals("test handle 1", handle1.id)
        assertEquals("test handle 2", handle2.id)
        assertEquals("test handle 3", handle3.id)
        set -= handle1
        set -= listOf(handle2, handle3)
        assertTrue { set.isEmpty() }
        set += handle1
        set += listOf(handle1, handle2)
        assertEquals(2, set.size)
        set -= "test handle 1"
        set -= listOf("test handle 3", "test handle 4")
        assertNull(space["test handle 4"])
        assertEquals(handle2, set["test handle 2"])
        assertThrows<IllegalArgumentException> { set += space.handle }
    }

    @Test
    fun `handle set tag test`() {
        val set = HandleSet(space)
        set += listOf("test handle 1", "test handle 2", "test handle 3")
        val tag1 = HandleManager.tagSpace.createHandle("test tag 1")
        val tag2 = HandleManager.tagSpace.createHandle("test tag 2")
        val tag3 = HandleManager.tagSpace.createHandle("test tag 3")
        val it = set.iterator()
        it.next() += listOf(tag1, tag2, tag3)
        it.next() += listOf(tag2, tag3)
        it.next() += tag3
        val handle1 = space["test handle 1"]!!
        val handle2 = space["test handle 2"]!!
        val handle3 = space["test handle 3"]!!
        assertEquals(setOf(handle1, handle2, handle3), set.getByTag(tag3))
        assertEquals(setOf(handle1, handle2), set.getByTag("test tag 2"))
        handle3 += tag1
        assertEquals(setOf(handle1, handle3), set.getByTags(setOf(tag1, tag3)))
        assertEquals(setOf(handle1), set.getByTags(setOf("test tag 1", "test tag 2")))
    }
}
