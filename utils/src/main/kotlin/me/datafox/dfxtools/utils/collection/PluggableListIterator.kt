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
class PluggableListIterator<E>(
    override val delegate: MutableListIterator<E>,
    spec: PluggableSpec<E>
) : PluggableIterator<E>(delegate, spec), MutableListIterator<E> {
    private val beforeAdd get() = spec.beforeAdd
    private val afterAdd get() = spec.afterAdd

    override fun set(element: E) {
        if(current == null) {
            throw IllegalStateException()
        }
        beforeOperation()
        beforeRemove(current!!)
        beforeAdd(element)
        delegate.set(element)
        afterRemove(current!!)
        afterAdd(element)
        afterOperation()
        current = element
    }

    override fun add(element: E) {
        beforeOperation()
        beforeAdd(element)
        delegate.add(element)
        afterAdd(element)
        afterOperation()
        current = null
    }

    override fun hasPrevious(): Boolean = delegate.hasPrevious()

    override fun previous(): E {
        current = delegate.previous()
        return current!!
    }

    override fun nextIndex(): Int = delegate.nextIndex()

    override fun previousIndex(): Int = delegate.previousIndex()
}