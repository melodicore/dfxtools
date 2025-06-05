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
import me.datafox.dfxtools.utils.collection.PluggableMap
import me.datafox.dfxtools.utils.collection.PluggableMapSpec

/**
 * A mutable map for [Observable] values owned by an [observer] that is added to and removed from
 * [value.observers][Observable.observers] when an entry is added to or removed from this map. It can also
 * optionally invalidate the [observer] when entries are added, determined by [invalidateObserver].
 *
 * @param delegate Underlying map implementation.
 * @param observer [Observer] owner of this map.
 * @param invalidateObserver If `true`, modifications to this map call [Observer.invalidate].
 * @param identifier Identifier for this map to be used with [CyclicAwareCollection].
 * @constructor Creates a new observable map.
 *
 * @author Lauri "datafox" Heino
 */
class ObservableMap<K, V : Observable> @JvmOverloads constructor(
    delegate: MutableMap<K, V>,
    observer: Observer,
    invalidateObserver: Boolean = true,
    callInitialElements: Boolean = true,
    identifier: Any = Any(),
    private val map: PluggableMap<K, V> = PluggableMap(delegate, spec(observer, invalidateObserver, identifier)),
) : MutableMap<K, V> by map {
    init {
        if(callInitialElements) map.callInitialElements()
    }

    override fun equals(other: Any?): Boolean = map == other

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    companion object {
        fun <K, V : Observable> spec(observer: Observer, invalidateObserver: Boolean, identifier: Any): PluggableMapSpec<K, V> =
            ObservableSet.spec<V>(observer, invalidateObserver, identifier).toMapValueSpec()
    }
}