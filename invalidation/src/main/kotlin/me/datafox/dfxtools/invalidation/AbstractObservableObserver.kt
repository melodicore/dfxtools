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
import me.datafox.dfxtools.invalidation.property.InvalidatedProperty

/**
 * Abstract implementation of [Observable] that populates [observers] and [propertyHandler].
 *
 * @author Lauri "datafox" Heino
 */
abstract class AbstractObservableObserver : ObservableObserver {
    override val observers = CyclicAwareCollection(this)
    override val propertyHandler = InvalidatedProperty.Handler()
}
