package me.datafox.dfxtools.invalidation

/**
 * A helper interface for classes that extend both [Observable] and [Observer].
 *
 * @author Lauri "datafox" Heino
 */
interface ObservableObserver : Observable, Observer {
    override fun invalidate() {
        super.invalidate()
        onChanged()
    }
}