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
import me.datafox.dfxtools.utils.collection.PluggableSet
import me.datafox.dfxtools.utils.collection.PluggableSpec

/**
 * A mutable set for [Observable] elements owned by an [observer] that is added to and removed from
 * [element.observers][Observable.observers] when an element is added to or removed from this set. It can also
 * optionally invalidate the [observer] when elements are added, determined by [invalidateObserver].
 *
 * @param delegate Underlying set implementation.
 * @param observer [Observer] owner of this set.
 * @param invalidateObserver If `true`, modifications to this set call [Observer.invalidate].
 * @param callInitialElements If `true`, [observer] will be added to all initial values of [delegate].
 * @param identifier Identifier for this set to be used with [CyclicAwareCollection].
 * @constructor Creates a new observable set.
 * @author Lauri "datafox" Heino
 */
class ObservableSet<E : Observable>
@JvmOverloads
constructor(
    delegate: MutableSet<E>,
    observer: Observer,
    invalidateObserver: Boolean = true,
    callInitialElements: Boolean = true,
    identifier: Any = Any(),
    private val set: PluggableSet<E> = PluggableSet(delegate, spec(observer, invalidateObserver, identifier)),
) : MutableSet<E> by set {
    init {
        if (callInitialElements) set.callInitialElements()
    }

    override fun equals(other: Any?): Boolean = set == other

    override fun hashCode(): Int = set.hashCode()

    override fun toString(): String = set.toString()

    companion object {
        fun <E : Observable> spec(observer: Observer, invalidateObserver: Boolean, identifier: Any): PluggableSpec<E> =
            PluggableSpec(
                beforeAdd = { it.observers.add(observer, identifier) },
                beforeRemove = { it.observers.remove(observer, identifier) },
                afterOperation = { if (invalidateObserver) observer.invalidate() },
            )
    }
}
