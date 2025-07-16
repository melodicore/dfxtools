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

package me.datafox.dfxtools.entities.reference

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.EntityData
import kotlin.reflect.KClass

/** @author Lauri "datafox" Heino */
@Polymorphic
interface DataFilter {
    fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean

    @Serializable
    @SerialName("all")
    data object All : DataFilter {
        override fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean = true
    }

    @Serializable
    @SerialName("handle")
    data class Handle(val filter: HandleFilter) : DataFilter {
        override fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean = filter.matches(data.handle)
    }

    @Serializable
    @SerialName("saved")
    data class Saved(val saved: Boolean) : DataFilter {
        override fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean = saved == data.saved
    }

    @Serializable
    @SerialName("and")
    data class And(val filters: List<DataFilter>) : DataFilter {
        override fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean =
            filters.all { it.matches(type, data) }
    }

    @Serializable
    @SerialName("or")
    data class Or(val filters: List<DataFilter>) : DataFilter {
        override fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean =
            filters.any { it.matches(type, data) }
    }

    @Serializable
    @SerialName("not")
    data class Not(val filter: DataFilter) : DataFilter {
        override fun <T : Any> matches(type: KClass<T>, data: EntityData<T>): Boolean = !filter.matches(type, data)
    }
}
