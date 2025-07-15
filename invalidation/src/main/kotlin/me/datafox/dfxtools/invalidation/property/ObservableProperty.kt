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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Property that automatically adds its owner to its value's [Observable.observers].
 *
 * @property value [Observable] value.
 * @property invalidateOwner If `true`, setting the value calls [Observer.invalidate].
 * @author Lauri "datafox" Heino
 */
class ObservableProperty<T : Observable>
@JvmOverloads
constructor(var value: T, private val invalidateOwner: Boolean = true) :
    ReadWriteProperty<Observer, T> {
    private val identifier = Any()

    override fun getValue(thisRef: Observer, property: KProperty<*>): T = value

    override fun setValue(thisRef: Observer, property: KProperty<*>, value: T) {
        if (value === this.value) return
        this.value.observers.remove(thisRef, identifier)
        value.observers.add(thisRef, identifier)
        this.value = value
        if (invalidateOwner) thisRef.invalidate()
    }

    operator fun provideDelegate(
        thisRef: Observer,
        property: KProperty<*>,
    ): ReadWriteProperty<Observer, T> {
        value.observers.add(thisRef, identifier)
        if (invalidateOwner) thisRef.invalidate()
        return this
    }
}
