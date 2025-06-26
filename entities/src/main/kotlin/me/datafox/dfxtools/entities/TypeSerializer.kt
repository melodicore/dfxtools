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

package me.datafox.dfxtools.entities

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.datafox.dfxtools.entities.Serialization.convertersByClass
import me.datafox.dfxtools.entities.Serialization.convertersByHandle
import me.datafox.dfxtools.handles.contains
import me.datafox.dfxtools.handles.get
import kotlin.reflect.KClass

/**
 * @author Lauri "datafox" Heino
 */
class TypeSerializer() : KSerializer<KClass<out Any>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("type", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: KClass<out Any>) {
        if(value !in convertersByClass) throw IllegalStateException()
        encoder.encodeString(convertersByClass[value]!!.handle.id)
    }

    override fun deserialize(decoder: Decoder): KClass<out Any> {
        val str = decoder.decodeString()
        if(str !in convertersByHandle) throw IllegalStateException()
        return convertersByHandle[str] as KClass<out Any>
    }
}