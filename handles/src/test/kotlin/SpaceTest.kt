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

import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.Purge
import me.datafox.dfxtools.handles.Space
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

/**
 * Tests for [Space].
 *
 * @author Lauri "datafox" Heino
 */
class SpaceTest {
    @OptIn(Purge::class)
    @AfterEach
    fun cleanup() {
        HandleManager.purge()
    }

    @Test
    fun `create space test`() {
        val space: Space = HandleManager.createSpace("test space")
        assertThrows<IllegalArgumentException> { HandleManager.createSpace("spaces") }
        assertThrows<IllegalArgumentException> { HandleManager.createSpace("tags") }
        assertThrows<IllegalArgumentException> { HandleManager.createSpace("test space") }
        val handle = HandleManager.getOrCreateQualifiedHandle("test handle@another space")
        assertThrows<IllegalArgumentException> { HandleManager.createSpace("another space") }
    }
}
