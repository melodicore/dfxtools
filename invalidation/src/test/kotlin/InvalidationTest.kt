
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
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

    private class BasicObservableObserver : AbstractObservableObserver() {
        override fun onInvalidated() { /* no-op */ }
    }
}