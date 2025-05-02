package me.datafox.dfxtools.invalidation

/**
 * An interface for classes that may be invalidated by other classes that it depends on. [onInvalidated] will be called
 * when this class gets invalidated, but keep in mind that this function may be called an arbitrary number of times, so
 * it is recommended to use it to set a flag, and do expensive calculations lazily elsewhere. Properties may be
 * delegated to [invalidated], which does this automatically. To invalidate this class manually, call [invalidate].
 *
 * @property invalidatedProperties set of delegated invalidated properties. This set should not be accessed manually.
 *
 * @author datafox
 */
interface Observer {
    val invalidatedProperties: MutableSet<InvalidatedProperty<*>>

    /**
     * This function is called when an [Observable] that this class depends on is changed. This function may be called
     * an arbitrary times.
     */
    fun onInvalidated()

    /**
     * This function is called when an [Observable] that this class depends on is changed. Do not override this function
     * for custom logic, override [onInvalidated] instead.
     */
    fun invalidate() {
        invalidatedProperties.forEach { it.invalidate() }
        onInvalidated()
    }

    fun <T> invalidated(value: T? = null, calculation: () -> T): InvalidatedProperty<T> {
        val property = InvalidatedProperty(value, calculation)
        invalidatedProperties.add(property)
        return property
    }
}