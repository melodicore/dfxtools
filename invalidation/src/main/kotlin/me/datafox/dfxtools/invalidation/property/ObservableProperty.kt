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

package me.datafox.dfxtools.invalidation.property

import me.datafox.dfxtools.invalidation.Observable
import me.datafox.dfxtools.invalidation.Observer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Property that automatically adds its owner to its value's [Observable.observers].
 *
 * @property value [Observable] value.
 *
 * @author Lauri "datafox" Heino
 */
class ObservableProperty(val value: Observable) : ReadOnlyProperty<Observer, Observable> {
    override fun getValue(thisRef: Observer, property: KProperty<*>): Observable = value

    operator fun provideDelegate(thisRef: Observer, property: KProperty<*>): ReadOnlyProperty<Observer, Observable> {
        value.observers.add(thisRef)
        return this
    }
}