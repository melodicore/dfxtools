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

package me.datafox.dfxtools.entities.serialization

import me.datafox.dfxtools.entities.Engine.dataTypeSpace
import me.datafox.dfxtools.handles.Handle
import kotlin.reflect.KClass

/**
 * @author Lauri "datafox" Heino
 */
class SimpleConverter<T : Any>(
    handle: String,
    override val type: KClass<T>,
    private val convertToType: (String) -> T,
    private val convertToData: (T) -> String
) : Converter<T> {
    override val handle: Handle = dataTypeSpace.getOrCreateHandle(handle)
    override fun convert(params: Map<String, String>): T = convertToType(params[VALUE]!!)
    override fun convert(value: T): Map<String, String> = mapOf(VALUE to convertToData(value))

    companion object {
        const val VALUE = "value"
    }
}