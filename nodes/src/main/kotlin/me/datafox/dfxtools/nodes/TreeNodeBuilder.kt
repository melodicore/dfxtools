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

/**
 * @author Lauri "datafox" Heino
 */
class TreeNodeBuilder internal constructor(
    private val nodeIndices: Map<String, Int>,
    private val inputIndices: Map<String, Map<String, Int>>,
    private val outputIndices: MutableMap<String, Int>
) {
    private lateinit var node: Node
    private val inputs: MutableList<NodeInputInfo<*>> = mutableListOf()
    private val outputs: MutableList<NodeOutputInfo<*>> = mutableListOf()
    private val connections: MutableList<NodeConnection> = mutableListOf()

    fun <T : Any> input(nodeId: String, outputId: String, type: NodeType<T>, vararg variants: NodeType.Variant<T>) {
        connections.add(NodeConnection(nodeIndices[nodeId]!!, inputIndices[nodeId]!![outputId]!!))
        inputs.add(NodeInputInfo(type, variants.toSet()))
    }

    fun <T : Any> output(id: String, type: NodeType<T>, variant: NodeType.Variant<T>) {
        outputIndices.put(id, outputs.size)
        outputs.add(NodeOutputInfo(type, variant))
    }

    fun build() : TreeNode {
        TODO()
    }
}