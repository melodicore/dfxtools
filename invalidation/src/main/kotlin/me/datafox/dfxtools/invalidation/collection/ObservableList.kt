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

/**
 * A mutable list for [Observable] values owned by an [Observer] that adds values to [Observable.observers] when they
 * are added to this list. Note that the values are *not* removed from observers when they are removed from this list.
 *
 * @property delegate underlying list implementation.
 * @property observer owner of this list.
 * @constructor Creates a new observable list.
 */
class ObservableList<E : Observable> private constructor(
    private val _delegate: MutableList<E>,
    observer: Observer,
    invalidateObserver: Boolean = true,
    uniqueIdentifier: Any = Any()
) : ObservableCollection<E>(_delegate, observer, invalidateObserver, uniqueIdentifier), MutableList<E> {
    constructor(delegate: MutableList<E>, owner: Observer, invalidateObserver: Boolean) :
            this(delegate, owner, invalidateObserver, Any())

    override fun remove(element: E): Boolean {
        if(isOnlyElement(element)) {
            element.observers.remove(observer, uniqueIdentifier)
        }
        if(_delegate.remove(element)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        elements.filter { isOnlyElement(it) }.forEach { it.observers.remove(observer, uniqueIdentifier) }
        if(_delegate.removeAll(elements)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        elements.forEach { it.observers.add(observer, uniqueIdentifier) }
        if(_delegate.addAll(index, elements)) {
            if(invalidateObserver) {
                observer.invalidate()
            }
            return true
        }
        return false
    }

    override fun set(index: Int, element: E): E {
        val removed = _delegate[index]
        if(removed !== element) {
            if(isOnlyElement(removed)) {
                removed.observers.remove(observer, uniqueIdentifier)
            }
            element.observers.add(observer, uniqueIdentifier)
            _delegate[index] = element
            if(invalidateObserver) {
                observer.invalidate()
            }
        }
        return removed
    }

    override fun add(index: Int, element: E) {
        element.observers.add(observer, uniqueIdentifier)
        _delegate.add(index, element)
        if(invalidateObserver) {
            observer.invalidate()
        }
    }

    override fun removeAt(index: Int): E {
        val removed = _delegate[index]
        if(isOnlyElement(removed)) {
            removed.observers.remove(observer, uniqueIdentifier)
        }
        _delegate.removeAt(index)
        if(invalidateObserver) {
            observer.invalidate()
        }
        return removed
    }

    override fun get(index: Int): E = _delegate[index]

    override fun indexOf(element: E): Int = _delegate.indexOf(element)

    override fun lastIndexOf(element: E): Int = _delegate.lastIndexOf(element)

    override fun iterator(): MutableIterator<E> = ObservableListIterator(this)

    override fun listIterator(): MutableListIterator<E> = ObservableListIterator(this)

    override fun listIterator(index: Int): MutableListIterator<E> = ObservableListIterator(this, index)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        ObservableList(_delegate.subList(fromIndex, toIndex),
            observer, invalidateObserver, uniqueIdentifier)

    private fun isOnlyElement(element: E): Boolean {
        return _delegate.indexOf(element) == _delegate.lastIndexOf(element)
    }

    private class ObservableListIterator<E: Observable>(private val owner: ObservableList<E>, index: Int = 0) : MutableListIterator<E> {
        private val delegate = owner._delegate.listIterator(index)
        private lateinit var current: E

        override fun next(): E {
            current = delegate.next()
            return current
        }

        override fun hasNext(): Boolean = delegate.hasNext()

        override fun remove() {
            if(owner.isOnlyElement(current)) {
                current.observers.remove(owner.observer, owner.uniqueIdentifier)
            }
            delegate.remove()
            if(owner.invalidateObserver) {
                owner.observer.invalidate()
            }
        }

        override fun set(element: E) {
            if(current !== element) {
                if(owner.isOnlyElement(current)) {
                    current.observers.remove(owner.observer, owner.uniqueIdentifier)
                }
                element.observers.add(owner.observer, owner.uniqueIdentifier)
                delegate.set(element)
                if(owner.invalidateObserver) {
                    owner.observer.invalidate()
                }
            }
        }

        override fun add(element: E) {
            element.observers.add(owner.observer, owner.uniqueIdentifier)
            delegate.add(element)
            if(owner.invalidateObserver) {
                owner.observer.invalidate()
            }
        }

        override fun hasPrevious(): Boolean = delegate.hasNext()

        override fun previous(): E {
            current = delegate.previous()
            return current
        }

        override fun nextIndex(): Int = delegate.nextIndex()

        override fun previousIndex(): Int = delegate.previousIndex()
    }
}