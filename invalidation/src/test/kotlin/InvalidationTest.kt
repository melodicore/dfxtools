
import me.datafox.dfxtools.invalidation.InvalidatedProperty
import me.datafox.dfxtools.invalidation.ObservableObserver
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

/**
 * @author datafox
 */
class InvalidationTest {
    @Test
    fun `cyclic dependency detection test`() {
        val first = BasicObservableObserver()
        val second = BasicObservableObserver()
        val third = BasicObservableObserver()
        first.observers.add(second)
        second.observers.add(third)
        assertThrows<IllegalArgumentException> { third.observers.add(first) }
    }

    private class BasicObservableObserver : ObservableObserver {
        override val observers = observers()

        override val invalidatedProperties: MutableSet<InvalidatedProperty<*>> = mutableSetOf()

        override fun onInvalidated() { /* no-op */ }
    }
}