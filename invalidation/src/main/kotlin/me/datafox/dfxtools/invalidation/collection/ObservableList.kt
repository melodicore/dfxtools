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
import me.datafox.dfxtools.utils.collection.PluggableList
import me.datafox.dfxtools.utils.collection.PluggableSpec

/**
 * A mutable list for [Observable] elements owned by an [observer] that is added to and removed from
 * [element.observers][Observable.observers] when an element is added to or removed from this list. It can also
 * optionally invalidate the [observer] when elements are added, determined by [invalidateObserver].
 *
 * @param delegate Underlying list implementation.
 * @param observer [Observer] owner of this list.
 * @param invalidateObserver If `true`, modifications to this list call [Observer.invalidate].
 * @param callInitialElements If `true`, [observer] will be added to all initial values of [delegate].
 * @param identifier Identifier for this list to be used with [CyclicAwareCollection].
 * @constructor Creates a new observable list.
 *
 * @author Lauri "datafox" Heino
 */
class ObservableList<E : Observable>(
    delegate: MutableList<E>,
    observer: Observer,
    invalidateObserver: Boolean = true,
    callInitialElements: Boolean = true,
    identifier: Any = Any(),
    list: PluggableList<E> = PluggableList(
        delegate = delegate,
        spec = spec(observer, invalidateObserver, identifier)
    )
) : MutableList<E> by list {
    init {
        if(callInitialElements) list.callInitialElements()
    }

    companion object {
        fun <E : Observable> spec(observer: Observer, invalidateObserver: Boolean, identifier: Any): PluggableSpec<E> =
            ObservableSet.spec(observer, invalidateObserver, identifier)
    }
}