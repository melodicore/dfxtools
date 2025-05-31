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
import me.datafox.dfxtools.nodes.internal.Utils.validateInputData
import me.datafox.dfxtools.nodes.internal.Utils.validateNodeTree
import me.datafox.dfxtools.nodes.internal.Utils.validateOutputData

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
data class NodeTree(
    val inputs: List<NodeInputInfo<*>>,
    val outputs: List<NodeOutputInfo<*>>,
    val nodes: List<TreeNode>,
    val outputConnections: List<NodeConnection>
) {
    init {
        validateNodeTree(inputs, outputs, nodes, outputConnections, logger)
    }

    fun accept(params: List<NodeData<*>>): List<NodeData<*>> {
        validateInputData(inputs, params, logger)
        val resolved: MutableList<List<NodeData<*>>> = mutableListOf()
        for((node, connections) in nodes) {
            val nodeParams = resolveParams(connections, resolved, params)
            resolved.add(node.accept(nodeParams))
        }
        val output = resolveParams(outputConnections, resolved, params)
        validateOutputData(outputs, output, logger)
        return output
    }

    private fun resolveParams(
        connections: List<NodeConnection>,
        resolved: List<List<NodeData<*>>>,
        params: List<NodeData<*>>
    ) : List<NodeData<*>> {
        val list = mutableListOf<NodeData<*>>()
        for((nodeIndex, dataIndex) in connections) {
            if(nodeIndex == -1) list.add(params[dataIndex])
            else list.add(resolved[nodeIndex][dataIndex])
        }
        return list
    }
}