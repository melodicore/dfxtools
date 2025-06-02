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
import me.datafox.dfxtools.nodes.internal.Utils.validateInputData

/**
 * @author Lauri "datafox" Heino
 */
class ConsumerNode(
    override val inputs: Map<String, NodeInputInfo<*>>,
    override val logger: KLogger,
    val consumerBlock: (Map<String, NodeData<*>>) -> Unit
) : Node {
    override val outputs: Map<String, NodeOutputInfo<*>> = emptyMap()
    override val block: (Map<String, NodeData<*>>) -> Map<String, NodeData<*>> = { consumerBlock(it); emptyMap() }

    override fun accept(params: Map<String, NodeData<*>>): Map<String, NodeData<*>> {
        validateInputData(inputs, params, logger)
        consumerBlock(params)
        return emptyMap()
    }
}