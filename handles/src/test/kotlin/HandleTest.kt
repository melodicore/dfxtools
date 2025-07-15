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
import kotlin.test.*

/**
 * Tests for [Handle].
 *
 * @author Lauri "datafox" Heino
 */
class HandleTest {
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
    fun `create handle test`() {
        val handle = space.createHandle("test handle")
        assertThrows<IllegalArgumentException> { space.createHandle("test handle") }
        assertSame(space.getOrCreateHandle("test handle"), handle)
        val subhandle1 = handle.createSubhandle("sub 1")
        assertThrows<IllegalArgumentException> { handle.createSubhandle("sub 1") }
        val subhandle2 = HandleManager.getOrCreateQualifiedHandle("test handle:sub 2@test space")
        assertTrue { subhandle2 in handle.subhandles!! }
    }

    @Test
    fun `tag test`() {
        val handle1 = space.createHandle("test handle 1")
        val handle2 = space.createHandle("test handle 2")
        val handle3 = space.createHandle("test handle 3")
        val tag1 = HandleManager.tagSpace.createHandle("tag 1")
        val tag2 = HandleManager.tagSpace.createHandle("tag 2")
        val tag3 = HandleManager.tagSpace.createHandle("tag 3")
        handle1 += listOf(tag1, tag2, tag3)
        handle2 += listOf(tag1, tag2)
        handle3 += tag1
        assertTrue { tag1 in handle1 }
        assertTrue { "tag 2" in handle2 }
        assertFalse { "tag 2" in handle3 }
        assertEquals(3, space.handles.getByTag(tag1).size)
        assertEquals(2, space.handles.getByTag(tag2).size)
        assertEquals(1, space.handles.getByTag(tag3).size)
        assertEquals(2, space.handles.getByTags(listOf(tag1, tag2)).size)
        assertEquals(1, space.handles.getByTags(listOf(tag1, tag3)).size)
        handle2 -= tag1
        assertEquals(1, space.handles.getByTags(listOf(tag1, tag2)).size)
        handle3 += "tag 4"
        val tag4 = HandleManager.tagSpace["tag 4"]!!
        handle2 += "tag 4"
        assertEquals(4, HandleManager.tagSpace.handles.size)
        val list1 = space.handles.getByTag("tag 4")
        val list2 = space.handles.getByTag(tag4)
        assertEquals(2, list1.size)
        assertEquals(list1, list2)
        assertEquals(1, space.handles.getByTags(listOf("tag 1", "tag 4")).size)
    }
}
