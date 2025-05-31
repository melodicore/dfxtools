package me.datafox.dfxtools.nodes

/**
 * @author Lauri "datafox" Heino
 */
data class TreeNode(
    val node: Node,
    val connections: List<NodeConnection>
)
