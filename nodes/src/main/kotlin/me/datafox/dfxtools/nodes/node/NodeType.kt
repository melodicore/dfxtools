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

package me.datafox.dfxtools.nodes.node

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.handles.Handled
import me.datafox.dfxtools.nodes.NodeManager.DEFAULT_VARIANT_ID
import me.datafox.dfxtools.nodes.NodeManager.typeSpace
import me.datafox.dfxtools.utils.Logging.logThrow
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
data class NodeType<T : Any>(
    override val handle: Handle,
    val type: KClass<T>,
    val variants: Map<Handle, Variant<T>>
) : Handled {
    init {
        if(typeSpace != handle.space) {
            logThrow(logger, "Node type handle must belong to space \"$typeSpace\"") { IllegalArgumentException(it) }
        }
        if(handle.parent != null) {
            logThrow(logger, "Node type handle must not be a subhandle") { IllegalArgumentException(it) }
        }
        variants.forEach { k, _ ->
            if(handle != k.parent) {
                logThrow(logger, "Node type variant handle must be a subhandle of the node type handle") { s -> IllegalArgumentException(s) }
            }
        }
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
            if(handle.parent == null) {
                logThrow(logger, "Node type variant handle must be a subhandle") { IllegalArgumentException(it) }
            }
        }

        fun isVariant(value: T): Boolean = value.predicate()
    }

    class Builder<T : Any> internal constructor(val handle: Handle, val type: KClass<T>) {
        private val variants: MutableList<Variant<T>> = mutableListOf()

        fun variant(id: String, predicate: T.() -> Boolean) {
            variants.add(Variant(handle.getOrCreateSubhandle(id), type, predicate))
        }

        internal fun build(): NodeType<T> {
            if(variants.isEmpty()) {
                variant(DEFAULT_VARIANT_ID) { true }
                logger.info { "No type variant was defined so a default one was created with id ${variants[0].handle.id}" }
            }
            return NodeType(handle, type, HandleMap(variants.associateBy { it.handle }))
        }
    }

    companion object {
        fun <T : Any> of(id: String, type: KClass<T>, builder: Builder<T>.() -> Unit = { }): NodeType<T> =
            Builder(typeSpace.getOrCreateHandle(id), type).apply(builder).build()
    }
}

