package me.datafox.dfxtools.nodes.node

/**
 * @author Lauri "datafox" Heino
 */
data class TreeNode(
    val node: Node,
    val connections: List<NodeConnection>
)
