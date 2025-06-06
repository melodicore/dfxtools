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
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.utils.collection.BiKeyMap
import java.lang.Boolean.parseBoolean
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat
import java.lang.Integer.parseInt
import java.lang.Long.parseLong
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

/**
 * @author Lauri "datafox" Heino
 */
object Serialization {
    private val _dataConverters: BiKeyMap<Handle, KClass<*>, Converter<*>> = BiKeyMap(HandleMap(dataTypeSpace))
    val convertersByHandle: Map<Handle, Converter<*>> = _dataConverters.first
    val convertersByClass: Map<KClass<*>, Converter<*>> = _dataConverters.second

    init {
        registerDefaultConverters()
    }

    fun <T : Any> registerConverter(converter: Converter<T>) {
        _dataConverters[converter.handle, converter.type] = converter
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getConverter(type: KClass<T>): Converter<T>? {
        return convertersByClass[type] as Converter<T>?
    }

    private fun registerDefaultConverters() {
        registerConverter(SimpleConverter(
            "boolean",
            Boolean::class,
            { parseBoolean(it) },
            { it.toString() }
        ))
        registerConverter(SimpleConverter(
            "string",
            String::class,
            { it },
            { it }
        ))
        registerConverter(SimpleConverter(
            "int",
            Int::class,
            { parseInt(it) },
            { it.toString() }
        ))
        registerConverter(SimpleConverter(
            "long",
            Long::class,
            { parseLong(it) },
            { it.toString() }
        ))
        registerConverter(SimpleConverter(
            "float",
            Float::class,
            { parseFloat(it) },
            { it.toString() }
        ))
        registerConverter(SimpleConverter(
            "double",
            Double::class,
            { parseDouble(it) },
            { it.toString() }
        ))
        registerConverter(SimpleConverter(
            "bigInt",
            BigInteger::class,
            { BigInteger(it) },
            { it.toString() }
        ))
        registerConverter(SimpleConverter(
            "bigDec",
            BigDecimal::class,
            { BigDecimal(it) },
            { it.toString() }
        ))
        registerConverter(LambdaConverter(
            "handle",
            Handle::class,
            { HandleManager.getOrCreateSpace(it["space"]!!).getOrCreateHandle(it["handle"]!!) },
            { mapOf("space" to it.space.handle.id, "handle" to it.id) }
        ))
        registerConverter(SimpleConverter(
            "space",
            Space::class,
            { HandleManager.getOrCreateSpace(it) },
            { it.handle.id }
        ))
        registerConverter(LambdaConverter(
            "group",
            Group::class,
            { HandleManager.getOrCreateSpace(it["space"]!!).getOrCreateGroup(it["group"]!!) },
            { mapOf("space" to it.space.handle.id, "group" to it.handle.id) }
        ))
    }
}