package me.datafox.dfxtools.nodes

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleMap
import me.datafox.dfxtools.nodes.NodeTypes.typeSpace
import me.datafox.dfxtools.utils.collection.BiKeyMap
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
object NodeManager {
    private val _types: BiKeyMap<Handle, KClass<*>, NodeType<out Any>> = BiKeyMap(HandleMap(typeSpace))
    val typesByHandle: Map<Handle, NodeType<*>> get() = _types.first
    val typesByClass: Map<KClass<*>, NodeType<*>> get() = _types.second

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getType(type: KClass<T>): NodeType<T>? = typesByClass[type] as NodeType<T>?

    internal fun <T : Any> registerType(type: NodeType<T>) {
        _types.put(type.handle, type.type, type)
    }
}