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

import me.datafox.dfxtools.invalidation.AbstractObservable
import me.datafox.dfxtools.invalidation.AbstractObserver
import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.property.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [properties][me.datafox.dfxtools.invalidation.property].
 *
 * @author Lauri "datafox" Heino
 */
class PropertyTest {
    @Test
    fun `invalidated property test`() {
        val test = TestInvalidatedProperty()
        assertEquals(0, test.counter)
        test.invalidate()
        assertEquals(1, test.counter)
        test.invalidate()
        test.invalidate()
        test.invalidate()
        assertEquals(2, test.counter)
        assertEquals(2, test.counter)
        test.invalidate()
        assertEquals(3, test.counter)
    }

    @Test
    fun `invalidator property test`() {
        val test = TestInvalidatorProperty()
        val observer = TestObserver()
        test.observers.add(observer, this)
        assertEquals(0, test.counter)
        assertEquals(0, observer.counter)
        test.counter++
        assertEquals(1, test.counter)
        assertEquals(1, observer.counter)
        test.counter = 42
        assertEquals(42, test.counter)
        assertEquals(2, observer.counter)
    }

    @Test
    fun `observable property test`() {
        val observable = TestObservable()
        val test = TestObservableProperty(observable)
        assertEquals(1, test.counter)
        observable.onChanged()
        assertEquals(2, test.counter)
    }

    @Test
    fun `observable list property test`() {
        val observable1 = TestObservable()
        val observable2 = TestObservable()
        val test = TestObservableListProperty()
        test.observables.add(observable1)
        assertEquals(1, test.counter)
        observable1.onChanged()
        assertEquals(2, test.counter)
        observable2.onChanged()
        assertEquals(2, test.counter)
        test.observables.addAll(listOf(observable1, observable2))
        assertEquals(3, test.observables.size)
        assertEquals(3, test.counter)
        observable1.onChanged()
        assertEquals(4, test.counter)
        observable2.onChanged()
        assertEquals(5, test.counter)
    }

    @Test
    fun `observable set property test`() {
        val observable1 = TestObservable()
        val observable2 = TestObservable()
        val test = TestObservableSetProperty()
        test.observables.add(observable1)
        assertEquals(1, test.counter)
        observable1.onChanged()
        assertEquals(2, test.counter)
        observable2.onChanged()
        assertEquals(2, test.counter)
        test.observables.addAll(listOf(observable1, observable2))
        assertEquals(2, test.observables.size)
        assertEquals(3, test.counter)
        observable1.onChanged()
        assertEquals(4, test.counter)
        observable2.onChanged()
        assertEquals(5, test.counter)
    }

    @Test
    fun `observable sorted set property test`() {
        val observable1 = TestComparableObservable(-1)
        val observable2 = TestComparableObservable(-2)
        val test = TestObservableSortedSetProperty()
        test.observables.add(observable1)
        assertEquals(1, test.counter)
        observable1.onChanged()
        assertEquals(2, test.counter)
        observable2.onChanged()
        assertEquals(2, test.counter)
        test.observables.addAll(listOf(observable1, observable2))
        assertEquals(2, test.observables.size)
        val it = test.observables.iterator()
        assertEquals(observable2, it.next())
        assertEquals(observable1, it.next())
        assertEquals(3, test.counter)
        observable1.onChanged()
        assertEquals(4, test.counter)
        observable2.onChanged()
        assertEquals(5, test.counter)
    }


    private class TestInvalidatedProperty : AbstractObserver() {
        val counter: Int by InvalidatedProperty { it?.plus(1) ?: 0 }

        override fun onInvalidated() { /* no-op */ }
    }

    private class TestInvalidatorProperty : AbstractObservable() {
        var counter: Int by InvalidatorProperty(0)
    }

    private class TestObserver : AbstractObserver() {
        var counter = 0

        override fun onInvalidated() { counter++ }
    }

    private class TestObservableProperty(observable: Observable) : AbstractObserver() {
        val observable by ObservableProperty(observable)
        var counter = 0

        override fun onInvalidated() { counter++ }
    }

    private class TestObservable : AbstractObservable()

    private class TestObservableListProperty : AbstractObserver() {
        val observables: MutableList<TestObservable> by ObservableListProperty()
        var counter = 0

        override fun onInvalidated() { counter++ }
    }

    private class TestObservableSetProperty : AbstractObserver() {
        val observables: MutableSet<TestObservable> by ObservableSetProperty()
        var counter = 0

        override fun onInvalidated() { counter++ }
    }

    private class TestObservableSortedSetProperty : AbstractObserver() {
        val observables: MutableSet<TestComparableObservable> by ObservableSortedSetProperty { e1, e2 -> e1.compareTo(e2) }
        var counter = 0

        override fun onInvalidated() { counter++ }
    }

    private class TestComparableObservable(val i: Int) : AbstractObservable(), Comparable<TestComparableObservable> {
        override fun compareTo(other: TestComparableObservable): Int = i.compareTo(other.i)
    }
}