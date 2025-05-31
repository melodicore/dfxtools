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
import me.datafox.dfxtools.utils.Logging.logThrow

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
class NodeTreeBuilder internal constructor() {
    private val inputs: MutableList<NodeInputInfo<*>> = mutableListOf()
    private val outputs: MutableList<NodeOutputInfo<*>> = mutableListOf()
    private val nodes: MutableList<TreeNode> = mutableListOf()
    private val nodeIndices: MutableMap<String, Int> = mutableMapOf()
    private val inputIndices: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()
    private val outputConnections: MutableList<NodeConnection> = mutableListOf()

    init {
        nodeIndices.put("", -1)
        inputIndices.put("", mutableMapOf())
    }

    fun <T : Any> input(id: String, type: NodeType<T>, vararg variants: NodeType.Variant<T>) {
        inputIndices[""]!!.put(id, inputs.size)
        inputs.add(NodeInputInfo(type, variants.toSet()))
    }

    fun node(id: String, block: TreeNodeBuilder.() -> Unit) {
        if(nodeIndices.containsKey(id)) {
            logThrow(logger, "Node with id $id already exists") { IllegalArgumentException(it) }
        }
        nodeIndices.put(id, nodes.size)
        val map: MutableMap<String, Int> = mutableMapOf()
        inputIndices.put(id, map)
        nodes.add(TreeNodeBuilder(nodeIndices, inputIndices, map).apply(block).build())
    }

    fun <T : Any> output(nodeId: String, inputId: String, type: NodeType<T>, variant: NodeType.Variant<T>) {
        outputConnections.add(NodeConnection(nodeIndices[nodeId]!!, inputIndices[nodeId]!![inputId]!!))
        outputs.add(NodeOutputInfo(type, variant))
    }
}