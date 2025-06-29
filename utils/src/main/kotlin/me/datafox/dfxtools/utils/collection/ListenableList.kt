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

/** @author Lauri "datafox" Heino */
interface ListenableList<E> : MutableList<E> {
    val view: View<E>

    fun addListener(listener: CollectionListener<E>): Boolean

    fun removeListener(listener: CollectionListener<E>): Boolean

    companion object {
        @JvmOverloads
        operator fun <E> invoke(
            beforeSpec: PluggableSpec<E>? = null,
            afterSpec: PluggableSpec<E>? = null,
            delegate: MutableList<E> = mutableListOf(),
        ): ListenableList<E> = Impl(delegate, beforeSpec, afterSpec, mutableSetOf())
    }

    private class Impl<E>(
        private val delegate: MutableList<E>,
        private val beforeSpec: PluggableSpec<E>?,
        private val afterSpec: PluggableSpec<E>?,
        private val listeners: MutableSet<CollectionListener<E>>,
        private val list: PluggableList<E> =
            PluggableList(delegate, ListenableSet.spec(beforeSpec, afterSpec, listeners)),
    ) : ListenableList<E>, MutableList<E> by list {
        override val view by lazy { View(this) }

        override fun addListener(listener: CollectionListener<E>): Boolean = listeners.add(listener)

        override fun removeListener(listener: CollectionListener<E>): Boolean =
            listeners.remove(listener)

        override fun equals(other: Any?): Boolean = delegate == other

        override fun hashCode(): Int = delegate.hashCode()

        override fun toString(): String = delegate.toString()
    }

    @Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
    class View<out E>(private val owner: ListenableList<E>) : List<E> by owner {
        fun addListener(listener: CollectionListener<@UnsafeVariance E>): Boolean =
            owner.addListener(listener)

        fun removeListener(listener: CollectionListener<@UnsafeVariance E>): Boolean =
            owner.removeListener(listener)

        override fun equals(other: Any?): Boolean = owner == other

        override fun hashCode(): Int = owner.hashCode()

        override fun toString(): String = owner.toString()
    }
}
