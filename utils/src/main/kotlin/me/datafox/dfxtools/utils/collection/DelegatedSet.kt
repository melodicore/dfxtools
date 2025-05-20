package me.datafox.dfxtools.utils.collection

/**
 * @author datafox
 */
abstract class DelegatedSet<out E> : DelegatedCollection<E>(), Set<E> {
    abstract override val delegate: Set<E>
}