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

import io.github.oshai.kotlinlogging.KLogger

/**
 * @author Lauri "datafox" Heino
 */
class NodeTreeNode(
    override val name: String,
    override val description: String,
    val tree: NodeTree,
    override val logger: KLogger,
) : Node {
    override val inputs: List<NodeInputInfo<*>> = tree.inputs
    override val outputs: List<NodeOutputInfo<*>> = tree.outputs
    override val block: (List<NodeData<*>>) -> List<NodeData<*>> = { throw UnsupportedOperationException() }

    override fun accept(params: List<NodeData<*>>): List<NodeData<*>> = tree.accept(params)
}