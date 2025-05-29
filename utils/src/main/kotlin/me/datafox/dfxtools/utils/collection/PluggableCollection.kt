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

import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.function.Predicate

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
abstract class PluggableCollection<E>(
    protected open val delegate: MutableCollection<E>,
    protected open val spec: PluggableSpec<E>
) : MutableCollection<E> {
    override val size: Int get() = delegate.size
    protected val beforeAdd get() = spec.beforeAdd
    protected val afterAdd get() = spec.afterAdd
    protected val beforeRemove get() = spec.beforeRemove
    protected val afterRemove get() = spec.afterRemove
    protected val beforeOperation get() = spec.beforeOperation
    protected val afterOperation get() = spec.afterOperation

    fun callInitialElements() {
        if(delegate.isNotEmpty()) {
            beforeOperation()
            delegate.forEach { beforeAdd(it) }
            delegate.forEach { afterAdd(it) }
            afterOperation()
        }
    }

    override fun remove(element: E): Boolean {
        val it = delegate.find { element == it } ?: return false
        beforeOperation()
        beforeRemove(it)
        delegate.remove(it)
        afterRemove(it)
        afterOperation()
        return true
    }

    override fun removeIf(filter: Predicate<in E>): Boolean {
        val its = delegate.filter { filter.test(it) }
        if(its.isEmpty()) return false
        beforeOperation()
        its.forEach { beforeRemove(it) }
        delegate.removeAll(its)
        its.forEach { afterRemove(it) }
        afterOperation()
        return true
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return removeIf { it in elements }
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return removeIf { it !in elements }
    }

    override fun clear() {
        val its = delegate.toList()
        beforeOperation()
        its.forEach { beforeRemove(it) }
        delegate.clear()
        its.forEach { afterRemove(it) }
        afterOperation()
    }

    override fun iterator(): MutableIterator<E> =
        PluggableIterator(delegate.iterator(), spec)

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun contains(element: E): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = delegate.containsAll(elements)
}
