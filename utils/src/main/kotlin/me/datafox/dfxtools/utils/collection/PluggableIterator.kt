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
open class PluggableIterator<E>(
    protected open val delegate: MutableIterator<E>,
    protected open val spec: PluggableSpec<E>
) : MutableIterator<E> {
    protected var current: E? = null
    protected val beforeRemove get() = spec.beforeRemove
    protected val afterRemove get() = spec.afterRemove
    protected val beforeOperation get() = spec.beforeOperation
    protected val afterOperation get() = spec.afterOperation

    override fun next(): E {
        current = delegate.next()
        return current!!
    }

    override fun hasNext(): Boolean = delegate.hasNext()

    override fun remove() {
        if(current == null) {
            throw NoSuchElementException()
        }
        beforeOperation()
        beforeRemove(current!!)
        delegate.remove()
        afterRemove(current!!)
        afterOperation()
        current = null
    }
}