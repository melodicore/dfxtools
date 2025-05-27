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
class PluggableList<E>(
    override val delegate: MutableList<E>,
    override val spec: PluggableSpec<E>
) : PluggableCollection<E>(
    delegate,
    spec
), MutableList<E> {
    override fun get(index: Int): E = delegate[index]

    override fun indexOf(element: E): Int = delegate.indexOf(element)

    override fun lastIndexOf(element: E): Int = delegate.lastIndexOf(element)

    override fun add(element: E): Boolean {
        add(delegate.size, element)
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        return addAll(delegate.size, elements)
    }

    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        if(index < 0 || index > delegate.size) {
            throw IndexOutOfBoundsException()
        }
        beforeOperation()
        elements.forEach { beforeAdd(it) }
        delegate.addAll(index, elements)
        elements.forEach { afterAdd(it) }
        afterOperation()
        return true
    }

    override fun set(index: Int, element: E): E {
        val old = delegate[index]
        beforeOperation()
        beforeAdd(element)
        beforeRemove(old)
        delegate[index] = element
        afterAdd(element)
        afterRemove(old)
        afterOperation()
        return old
    }

    override fun add(index: Int, element: E) {
        if(index < 0 || index > delegate.size) {
            throw IndexOutOfBoundsException()
        }
        beforeOperation()
        beforeAdd(element)
        delegate.add(index, element)
        afterAdd(element)
        afterOperation()
    }

    override fun removeAt(index: Int): E {
        val old = delegate[index]
        beforeOperation()
        beforeRemove(old)
        delegate.removeAt(index)
        afterRemove(old)
        afterOperation()
        return old
    }

    override fun listIterator(): MutableListIterator<E> =
        PluggableListIterator(delegate.listIterator(), spec)

    override fun listIterator(index: Int): MutableListIterator<E> =
        PluggableListIterator(delegate.listIterator(index), spec)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> =
        PluggableList(delegate.subList(fromIndex, toIndex), spec)
}