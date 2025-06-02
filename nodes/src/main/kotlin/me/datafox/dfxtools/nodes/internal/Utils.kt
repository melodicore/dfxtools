package me.datafox.dfxtools.nodes.internal

import io.github.oshai.kotlinlogging.KLogger
import me.datafox.dfxtools.nodes.*
import me.datafox.dfxtools.utils.Logging.logThrow

/**
 * @author Lauri "datafox" Heino
 */
internal object Utils {
    fun validateInputData(inputs: Map<String, NodeInputInfo<*>>, params: Map<String, NodeData<*>>, logger: KLogger) {
        inputs.forEach { id, info ->
            if(!params.containsKey(id)) {
                if(!info.optional) logThrow(
                    logger,
                    "Input parameter $id is not optional but is not present"
                ) { IllegalArgumentException(it) }
                return@forEach
            }
            val param = params[id]!!
            if(info.type != param.type) {
                logThrow(logger, "Input data type mismatch") { IllegalArgumentException(it) }
            }
            if(param.variants.none { it in info.allowedVariants }) {
                logThrow(logger, "Input data type variant mismatch") { IllegalArgumentException(it) }
            }
            validateInputData(param, info, logger)
        }
    }

    fun validateOutputData(outputs: Map<String, NodeOutputInfo<*>>, params: Map<String, NodeData<*>>, logger: KLogger) {
        if(outputs.size != params.size) {
            logger.warn { "Output data size mismatch, something may be wrong with node logic" }
        }
        outputs.forEach { id, info ->
            if(!params.containsKey(id)) {
                logThrow(logger, "Output parameter $id is not present") { IllegalArgumentException(it) }
            }
            val param = params[id]!!
            if(info.type != param.type) {
                logThrow(logger, "Output data type mismatch") { IllegalArgumentException(it) }
            }
            if(info.variant !in param.variants) {
                logThrow(logger, "Output data type variant mismatch") { IllegalArgumentException(it) }
            }
        }
    }

    fun validateNodeTree(nodes: List<NodeTree.TreeNode>, connections: Map<String, NodeTree.Connection>, logger: KLogger) {
        val resolved: MutableMap<String, Map<String, Pair<NodeType<*>, NodeType.Variant<*>>>> = mutableMapOf()
        nodes.forEach { (id, node) ->
            val connection = connections[id] ?: logThrow(logger, "Node tree connections mismatch") { IllegalArgumentException(it) }
            val params = node.inputs.forEach { pId, info ->
                val params = connection.params[pId]
                val types = resolved[params?.node]
                val type = types?.get(params?.output)
                if(type == null || !info.isAcceptedType(type.first, type.second)) {
                    logThrow(logger, "Node tree connections mismatch") { s -> IllegalArgumentException(s) }
                }
            }
            val outputs = node.outputs
                .map { Triple(it.key, it.value.type, it.value.variant) }
                .associate { it.first to Pair(it.second, it.third) }
            resolved.put(id, outputs)
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