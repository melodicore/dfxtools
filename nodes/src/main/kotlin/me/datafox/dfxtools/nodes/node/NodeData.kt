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

/**
 * @author Lauri "datafox" Heino
 */
data class NodeData<T : Any>(
    val type: NodeType<T>,
    val variant: NodeType.Variant<T>,
    val data: T
) {
    @Suppress("UNCHECKED_CAST")
    fun <R : Any> toKnownType(type: NodeType<R>): NodeData<R> {
        if(this.type != type) throw ClassCastException()
        return this as NodeData<R>
    }
}