package me.datafox.dfxtools.handles

/**
 * @author datafox
 */

operator fun Handle.plusAssign(id: String) {
    tags += id
}

operator fun Handle.plusAssign(ids: Iterable<String>) {
    tags += ids
}

operator fun Handle.minusAssign(id: String) {
    tags -= id
}

operator fun Handle.minusAssign(ids: Iterable<String>) {
    tags -= ids
}

fun Set<Handle>.getByTag(tag: String): Set<Handle> = mapNotNull { if(it.tags.contains(tag)) it else null }.toSet()

fun Set<Handle>.getByTags(tags: Iterable<String>): Set<Handle> =
    mapNotNull { if(it.tags.containsAll(tags)) it else null }.toSet()

fun <T> Map<Handle, T>.getByTag(tag: String): List<T> = mapNotNull { if(it.key.tags.contains(tag)) it.value else null }

fun <T> Map<Handle, T>.getByTags(tags: Iterable<String>): List<T> =
    mapNotNull { if(it.key.tags.containsAll(tags)) it.value else null }