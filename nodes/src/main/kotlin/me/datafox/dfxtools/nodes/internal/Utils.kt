package me.datafox.dfxtools.nodes.internal

import io.github.oshai.kotlinlogging.KLogger
import me.datafox.dfxtools.nodes.node.*
import me.datafox.dfxtools.utils.Logging.logThrow

/**
 * @author Lauri "datafox" Heino
 */
internal object Utils {
    fun validateNodeTree(inputs: List<NodeInputInfo<*>>,
                         outputs: List<NodeOutputInfo<*>>,
                         nodes: List<TreeNode>,
                         outputConnections: List<NodeConnection>,
                         logger: KLogger) {
        for(i in 0 until nodes.size + 1) {
            val isNode = i < nodes.size
            val inputs = if(isNode) nodes[i].node.inputs else inputs
            val connections = if(isNode) nodes[i].connections else outputConnections
            if(inputs.size != connections.size) {
                logThrow(logger, "Node tree mapping size mismatch") { IllegalArgumentException(it) }
            }
            val invalid = connections.filter { it.node >= i }
            if(invalid.isNotEmpty()) {
                logThrow(logger, "Node tree mapping mismatch") { IllegalArgumentException(it) }
            }
            for(i in 0 until inputs.size) {
                val (nodeIndex, dataIndex) = connections[i]
                val output = if(nodeIndex == -1) outputs[dataIndex] else nodes[nodeIndex].node.outputs[dataIndex]
                if(inputs[i].type != output.type) {
                    logThrow(logger, "Node tree data type mismatch") { IllegalArgumentException(it) }
                }
                if(output.variant !in inputs[i].allowedVariants) {
                    logThrow(logger, "Node tree data type variant mismatch") { IllegalArgumentException(it) }
                }
            }
        }
    }

    fun validateInputData(inputs: List<NodeInputInfo<*>>, params: List<NodeData<*>>, logger: KLogger) {
        if(inputs.size != params.size) {
            logThrow(logger, "Input data size mismatch") { IllegalArgumentException(it) }
        }
        for(i in 0 until inputs.size) {
            if(inputs[i].type != params[i].type) {
                logThrow(logger, "Input data type mismatch") { IllegalArgumentException(it) }
            }
            if(params[i].variant !in inputs[i].allowedVariants) {
                logThrow(logger, "Input data type variant mismatch") { IllegalArgumentException(it) }
            }
            if(inputs[i].validator != null) {
                validateInputData(params[i], inputs[i], logger)
            }
        }
    }

    fun validateOutputData(outputs: List<NodeOutputInfo<*>>, params: List<NodeData<*>>, logger: KLogger) {
        if(outputs.size != params.size) {
            logThrow(logger, "Output data size mismatch") { IllegalArgumentException(it) }
        }
        for(i in 0 until outputs.size) {
            if(outputs[i].type != params[i].type) {
                logThrow(logger, "Output data type mismatch") { IllegalArgumentException(it) }
            }
            if(outputs[i].variant != params[i].variant) {
                logThrow(logger, "Output data type variant mismatch") { IllegalArgumentException(it) }
            }
        }
    }

    private fun <T : Any> validateInputData(data: NodeData<*>, info: NodeInputInfo<T>, logger: KLogger) {
        val data = data.toKnownType(info.type)
        val message = info.validator?.invoke(data.data)
        if(message != null) {
            logThrow(logger, "Input data validator failed with message: $message") { IllegalArgumentException(it) }
        }
    }
}