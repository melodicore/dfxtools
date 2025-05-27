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
class PluggableSet<E>(
    override val delegate: MutableSet<E>,
    override val spec: PluggableSpec<E>
) : PluggableCollection<E>(
    delegate,
    spec
), MutableSet<E> {
    override fun add(element: E): Boolean {
        if(delegate.contains(element)) {
            return false
        }
        beforeOperation()
        beforeAdd(element)
        delegate.add(element)
        afterAdd(element)
        afterOperation()
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val its = elements.filter { it !in this }
        if(its.isEmpty()) {
            return false
        }
        beforeOperation()
        its.forEach { beforeAdd(it) }
        delegate.addAll(elements)
        its.forEach { afterAdd(it) }
        afterOperation()
        return true
    }
}