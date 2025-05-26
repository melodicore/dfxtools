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

/**
 * A mutable set for [Observable] values owned by an [Observer] that adds elements to [Observable.observers] when they
 * are added to this set. Note that the elements are *not* removed from observers when they are removed from this set.
 * It can also optionally invalidate the [observer] when elements are added, determined by [invalidateObserver].
 *
 * @property delegate underlying set implementation.
 * @property observer owner of this set.
 * @constructor Creates a new observable set.
 */
class ObservableSet<E : Observable>(
    delegate: MutableSet<E>,
    observer: Observer,
    invalidateObserver: Boolean
) : ObservableCollection<E>(delegate, observer, invalidateObserver), MutableSet<E>
