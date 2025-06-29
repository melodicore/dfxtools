package me.datafox.dfxtools.utils.collection

class UnsupportedRemoveIterator<E>(delegate: MutableIterator<E>) : MutableIterator<E> by delegate {
    override fun remove() = throw UnsupportedOperationException()
}
