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

package me.datafox.dfxtools.utils.collection

/**
 * @author Lauri "datafox" Heino
 */
class LateDelegatedSet<E> : MutableSet<E> {
    lateinit var delegate: MutableSet<E>
    override val size: Int get() = delegate.size

    override fun add(element: E): Boolean = delegate.add(element)

    override fun remove(element: E): Boolean = delegate.remove(element)

    override fun addAll(elements: Collection<E>): Boolean = delegate.addAll(elements)

    override fun removeAll(elements: Collection<E>): Boolean = delegate.removeAll(elements)

    override fun retainAll(elements: Collection<E>): Boolean = delegate.retainAll(elements)

    override fun clear() = delegate.clear()

    override fun iterator(): MutableIterator<E> = delegate.iterator()

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun contains(element: E): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = delegate.containsAll(elements)

    override fun equals(other: Any?): Boolean = delegate == other

    override fun hashCode(): Int = delegate.hashCode()

    override fun toString(): String = delegate.toString()
}