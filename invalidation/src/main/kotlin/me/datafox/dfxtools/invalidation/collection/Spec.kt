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
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import me.datafox.dfxtools.utils.collection.PluggableSpec

fun <E : Observable> observableSpec(observer: Observer, invalidateObserver: Boolean, identifier: Any): PluggableSpec<E> = PluggableSpec(
    beforeAdd = { it.observers.add(observer, identifier) },
    beforeRemove = { it.observers.remove(observer, identifier) },
    afterOperation = { if(invalidateObserver) observer.invalidate() }
)

fun <K, V : Observable> observableMapSpec(observer: Observer, invalidateObserver: Boolean, identifier: Any): PluggableMapSpec<K, V> =
    observableSpec<V>(observer, invalidateObserver, identifier).toMapValueSpec()