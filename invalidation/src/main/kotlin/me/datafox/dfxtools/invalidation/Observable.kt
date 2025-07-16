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

package me.datafox.dfxtools.invalidation

import me.datafox.dfxtools.invalidation.collection.CyclicAwareCollection
import me.datafox.dfxtools.invalidation.property.InvalidatorProperty

/**
 * An interface for classes that may invalidate other classes that depend on it. Whenever this class is changed, call
 * [onChanged]. [InvalidatorProperty] may be used to delegate variable properties, which calls [onChanged] when the
 * property is set to a new value.
 *
 * @property observers [CyclicAwareCollection] for [Observers][Observer] to detect cyclic dependencies and track objects
 *   that added the observer. Cyclic dependency detection only works when classes in the dependency chain implement both
 *   this interface and [Observer], preferably by implementing [ObservableObserver].
 * @author Lauri "datafox" Heino
 */
interface Observable {
    val observers: CyclicAwareCollection

    /**
     * Invalidates all observers. Call when something in this class has changed. [InvalidatorProperty] calls this
     * function automatically when set.
     */
    fun onChanged() = observers.forEach { it.invalidate() }
}
