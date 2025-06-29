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

package me.datafox.dfxtools.entities.extensions.modifier

import java.math.BigDecimal
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.reference.DataReference
import me.datafox.dfxtools.values.MarkerValue
import me.datafox.dfxtools.values.ModifiableValue
import me.datafox.dfxtools.values.SimpleValue
import me.datafox.dfxtools.values.Value

@Polymorphic
sealed interface ModifierParameter {
    fun get(): Value

    @Serializable
    data class Simple(val value: String) : ModifierParameter {
        private val _value by lazy { SimpleValue(BigDecimal(value)) }

        override fun get(): Value = _value
    }

    @Serializable
    data class Marker(val value: Int) : ModifierParameter {
        private val _value by lazy { MarkerValue(value) }

        override fun get(): Value = _value
    }

    @Serializable
    data class Reference(val ref: DataReference<@Contextual ModifiableValue>) : ModifierParameter {
        override fun get(): Value = ref.get()[0].data
    }
}
