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
import me.datafox.dfxtools.nodes.internal.Utils.validateNodeTree

private val logger = KotlinLogging.logger {}

/**
* @author Lauri "datafox" Heino
*/
class NodeTree(
    private val nodes: List<TreeNode>,
    private val connections: Map<String, Connection>
) {
    init {
        validateNodeTree(nodes, connections, logger)
    }

    fun run() {
        val resolved: MutableMap<String, Map<String, NodeData<*>>> = mutableMapOf()
        nodes.forEach { (id, node) ->
            val connection = connections[id]!!
            val params = node.inputs.keys.associateWith {
                val (node, output) = connection.params[it]!!
                resolved[node]!![output]!!
            }
            resolved.put(id, node.accept(params))
        }
    }

    data class TreeNode(val id: String, val node: Node)

    data class Connection(val params: Map<String, Param>)

    data class Param(val node: String, val output: String)
}