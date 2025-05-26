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

import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for general invalidation
 *
 * @author Lauri "datafox" Heino
 */
class InvalidationTest {
    @Test
    fun `invalidation test`() {
        val first = TestOO()
        val second = TestOO()
        val third = TestOO()
        val fourth = TestOO()
        val fifth = TestOO()
        first.observers.add(second, this)
        second.observers.add(third, this)
        second.observers.add(fourth, this)
        third.observers.add(fifth, this)
        third.observers.add(fourth, this)
        fourth.observers.add(fifth, this)
        first.invalidate()
        assertEquals(1, first.counter)
        assertEquals(1, second.counter)
        assertEquals(1, third.counter)
        assertEquals(2, fourth.counter)
        assertEquals(3, fifth.counter)
        second.invalidate()
        assertEquals(1, first.counter)
        assertEquals(2, second.counter)
        assertEquals(2, third.counter)
        assertEquals(4, fourth.counter)
        assertEquals(6, fifth.counter)
        third.invalidate()
        assertEquals(1, first.counter)
        assertEquals(2, second.counter)
        assertEquals(3, third.counter)
        assertEquals(5, fourth.counter)
        assertEquals(8, fifth.counter)
        fourth.invalidate()
        assertEquals(1, first.counter)
        assertEquals(2, second.counter)
        assertEquals(3, third.counter)
        assertEquals(6, fourth.counter)
        assertEquals(9, fifth.counter)
        fifth.invalidate()
        assertEquals(1, first.counter)
        assertEquals(2, second.counter)
        assertEquals(3, third.counter)
        assertEquals(6, fourth.counter)
        assertEquals(10, fifth.counter)
    }

    @Test
    fun `cyclic dependency detection test`() {
        val first = TestOO()
        assertThrows<IllegalArgumentException> { first.observers.add(first, this) }
        val second = TestOO()
        val third = TestOO()
        first.observers.add(second, this)
        second.observers.add(third, this)
        assertThrows<IllegalArgumentException> { third.observers.add(first, this) }
    }

    private class TestOO : AbstractObservableObserver() {
        var counter = 0

        override fun onInvalidated() {
            counter++
            onChanged()
        }
    }
}