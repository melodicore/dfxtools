package me.datafox.dfxtools.invalidation

/**
 * An interface for classes that may invalidate other classes that depend on it. Whenever this class is changed, call
 * [onChanged]. [invalidator] may be used to delegate variable properties, which calls [onChanged] when the property is
 * set to a new value.
 *
 * @property observers set of observers. Uses [CyclicAwareSet] to detect cyclic dependencies. Cyclic dependency
 * detection only works when classes in the dependency chain implement both this interface and [Observer], preferably by
 * implementing [ObservableObserver].
 *
 * @author datafox
 */
interface Observable {
    val observers: CyclicAwareSet

    /**
     * Invalidates all observers. Call when something in this class has changed. [invalidator] properties call this
     * function automatically when set.
     */
    fun onChanged() = observers.forEach { it.invalidate() }

    /**
     * Property delegate that calls [onChanged] when set.
     */
    fun <T> invalidator(value: T) = InvalidatorProperty(value)

    /**
     * Returns a [CyclicAwareSet] with this class as a parameter.
     */
    fun observers() = CyclicAwareSet(this)
}