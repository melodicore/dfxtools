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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property that will call [Observable.onChanged] when set.
 *
 * @property value Initial value for this property.
 * @property onInvalidate Extra logic that is run when this property is set, before [Observable.onChanged] is called.
 * @constructor Creates a new invalidator property.
 * @author Lauri "datafox" Heino
 */
class InvalidatorProperty<V>
@JvmOverloads
constructor(private var value: V, private val onInvalidate: () -> Unit = {}) : ReadWriteProperty<Observable, V> {
    override fun getValue(thisRef: Observable, property: KProperty<*>) = value

    override fun setValue(thisRef: Observable, property: KProperty<*>, value: V) {
        if (this.value == value) return
        this.value = value
        onInvalidate()
        thisRef.onChanged()
    }
}
