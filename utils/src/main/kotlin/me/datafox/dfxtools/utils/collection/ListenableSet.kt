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
interface ListenableSet<E> : MutableSet<E> {
    val view: View<E>

    fun addListener(listener: CollectionListener<E>): Boolean

    fun removeListener(listener: CollectionListener<E>): Boolean

    companion object {
        @JvmOverloads
        operator fun <E> invoke(
            beforeSpec: PluggableSpec<E>? = null,
            afterSpec: PluggableSpec<E>? = null,
            delegate: MutableSet<E> = mutableSetOf(),
        ): ListenableSet<E> = Impl(delegate, beforeSpec, afterSpec, mutableSetOf())

        fun <E> spec(
            beforeSpec: PluggableSpec<E>?,
            afterSpec: PluggableSpec<E>?,
            listeners: Set<CollectionListener<E>>,
        ): PluggableSpec<E> {
            val spec =
                PluggableSpec<E>(
                    afterAdd = { listeners.forEach { l -> l.onAdd(it) } },
                    afterRemove = { listeners.forEach { l -> l.onRemove(it) } },
                )
            if (beforeSpec != null) {
                if (afterSpec != null) return PluggableSpec(beforeSpec, spec, afterSpec)
                return PluggableSpec(beforeSpec, spec)
            }
            if (afterSpec != null) return PluggableSpec(spec, afterSpec)
            return spec
        }
    }

    private class Impl<E>(
        private val delegate: MutableSet<E>,
        private val beforeSpec: PluggableSpec<E>?,
        private val afterSpec: PluggableSpec<E>?,
        private val listeners: MutableSet<CollectionListener<E>>,
        private val set: PluggableSet<E> =
            PluggableSet(delegate, ListenableSet.spec(beforeSpec, afterSpec, listeners)),
    ) : ListenableSet<E>, MutableSet<E> by set {
        override val view by lazy { View(this) }

        override fun addListener(listener: CollectionListener<E>): Boolean = listeners.add(listener)

        override fun removeListener(listener: CollectionListener<E>): Boolean =
            listeners.remove(listener)

        override fun equals(other: Any?): Boolean = delegate == other

        override fun hashCode(): Int = delegate.hashCode()

        override fun toString(): String = delegate.toString()
    }

    @Suppress("JavaDefaultMethodsNotOverriddenByDelegation")
    class View<out E>(private val owner: ListenableSet<E>) : Set<E> by owner {
        fun addListener(listener: CollectionListener<@UnsafeVariance E>): Boolean =
            owner.addListener(listener)

        fun removeListener(listener: CollectionListener<@UnsafeVariance E>): Boolean =
            owner.removeListener(listener)

        override fun equals(other: Any?): Boolean = owner == other

        override fun hashCode(): Int = owner.hashCode()

        override fun toString(): String = owner.toString()
    }
}
