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

package me.datafox.dfxtools.invalidation.collection

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import java.util.function.Predicate

/**
 * @author Lauri "datafox" Heino
 */
@Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
abstract class ObservableCollection<E : Observable> protected constructor(
    protected val delegate: MutableCollection<E>,
    protected val observer: Observer,
    protected val invalidateObserver: Boolean,
    protected val uniqueIdentifier: Any = Any()
) : MutableCollection<E> by delegate {
    init {
        delegate.forEach { it.observers.add(observer, uniqueIdentifier) }
    }

    override fun add(element: E): Boolean {
        element.observers.add(observer, uniqueIdentifier)
        if(delegate.add(element)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(observer, uniqueIdentifier) }
        if(delegate.addAll(elements)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun clear() {
        delegate.forEach { it.observers.remove(observer, uniqueIdentifier) }
        if(delegate.isNotEmpty()) {
            delegate.clear()
            if(invalidateObserver) {
                observer.invalidate()
            }
        }
    }

    override fun remove(element: E): Boolean {
        element.observers.remove(observer, uniqueIdentifier)
        if(delegate.remove(element)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        elements.forEach { it.observers.remove(observer, uniqueIdentifier) }
        if(delegate.removeAll(elements)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        delegate.filterNot { elements.contains(it) }.forEach { it.observers.remove(observer, uniqueIdentifier) }
        if(delegate.retainAll(elements)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun removeIf(filter: Predicate<in E>): Boolean {
        delegate.filter { filter.test(it) }.forEach { it.observers.remove(observer, uniqueIdentifier) }
        if(delegate.removeIf(filter)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun iterator(): MutableIterator<E> = ObservableIterator(this)

    private class ObservableIterator<E: Observable>(private val owner: ObservableCollection<E>) : MutableIterator<E> {
        private val delegate = owner.delegate.iterator()
        private lateinit var current: E

        override fun next(): E {
            current = delegate.next()
            return current
        }

        override fun hasNext(): Boolean = delegate.hasNext()

        override fun remove() {
            current.observers.remove(owner.observer, owner.uniqueIdentifier)
            delegate.remove()
            if(owner.invalidateObserver) {
                owner.observer.invalidate()
            }
        }
    }
}