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

import io.github.oshai.kotlinlogging.KLogger
import me.datafox.dfxtools.nodes.internal.Utils.validateOutputData

/**
 * @author Lauri "datafox" Heino
 */
class SupplierNode(
    override val outputs: Map<String, NodeOutputInfo<*>>,
    override val logger: KLogger,
    val supplierBlock: () -> Map<String, NodeData<*>>
) : Node {
    override val inputs: Map<String, NodeInputInfo<*>> = emptyMap()
    override val block: (Map<String, NodeData<*>>) -> Map<String, NodeData<*>> = { supplierBlock() }

    fun accept(): Map<String, NodeData<*>> = accept(emptyMap())

    override fun accept(params: Map<String, NodeData<*>>): Map<String, NodeData<*>> {
        if(params.isNotEmpty()) {
            logger.warn { "Parameters given to supplier node, something may be wrong with node logic" }
        }
        val output = supplierBlock()
        validateOutputData(outputs, output, logger)
        return output
    }
}