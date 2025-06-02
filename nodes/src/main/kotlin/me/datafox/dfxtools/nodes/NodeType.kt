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

package me.datafox.dfxtools.nodes

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.nodes.NodeTypes.any
import me.datafox.dfxtools.nodes.NodeTypes.typeSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.BiKeyMap
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
class NodeType<T : Any> internal constructor(
    override val handle: Handle,
    val type: KClass<T>,
    val superType: NodeType<in T>?
) : Handled {
    private val _subTypes: BiKeyMap<Handle, KClass<out T>, NodeType<out T>> =
        BiKeyMap(HandleMap(typeSpace), mutableMapOf())
    private val _variants: HandleMap<Variant<T>> = HandleMap(typeSpace)
    val subTypesByHandle: Map<Handle, NodeType<out T>> = _subTypes.first
    val subTypes: Map<KClass<out T>, NodeType<out T>> = _subTypes.second
    val variants: Map<Handle, Variant<T>> = _variants.immutableView

    init {
        if(typeSpace != handle.space) {
            logThrow(logger, "Node type handle must belong to space \"$typeSpace\"") { IllegalArgumentException(it) }
        }
        if(handle.parent != null) {
            logThrow(logger, "Node type handle must not be a subhandle") { IllegalArgumentException(it) }
        }
        registerVariant(NodeTypes.DEFAULT_VARIANT_ID, { true })
        NodeManager.registerType(this)
    }

    @Suppress("MUTABLE_PROPERTY_WITH_CAPTURED_TYPE")
    fun <V : T>registerSubType(id: String, type: KClass<V>): NodeType<V> {
        if(any.subTypes.contains(type)) {
            logThrow(logger, "Type is already registered") { IllegalArgumentException(it) }
        }
        if(any.subTypesByHandle.contains(id)) {
            logThrow(logger, "Type handle is already registered") { IllegalArgumentException(it) }
        }
        return NodeType(typeSpace.getOrCreateHandle(id), type, this).apply { registerSubType(this) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : T> getSubType(type: KClass<V>): NodeType<V>? {
        return subTypes[type] as NodeType<V>?
    }

    fun registerVariant(id: String, predicate: T.() -> Boolean): Variant<T> {
        if(_variants.contains(id)) {
            logThrow(logger, "Type variant handle is already registered") { IllegalArgumentException(it) }
        }
        return Variant(handle.getOrCreateSubhandle(id), type, predicate).apply { _variants.putHandled(this) }
    }

    private fun registerSubType(type: NodeType<out T>) {
        _subTypes.put(type.handle, type.type, type)
        superType?.registerSubType(type)
    }

    data class Variant<T : Any>(
        override val handle: Handle,
        val type: KClass<T>,
        val predicate: T.() -> Boolean
    ) : Handled {
        init {
            if(typeSpace != handle.space) {
                logThrow(logger, "Node type variant handle must belong to space \"$typeSpace\"") { IllegalArgumentException(it) }
            }
            if(handle.parent != null) {
                logThrow(logger, "Node type variant handle must be a subhandle") { IllegalArgumentException(it) }
            }
        }

        fun isVariant(value: T): Boolean = type.isInstance(value) && value.predicate()
    }
}