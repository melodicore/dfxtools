
import me.datafox.dfxtools.handles.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * @author Lauri "datafox" Heino
 */
class HandlesTest {
    lateinit var space: Space
    lateinit var handle: Handle
    lateinit var subhandle: Handle

    @BeforeEach
    fun prepare() {
        space = HandleManager.createSpace("space")
        handle = space.createHandle("handle")
        subhandle = handle.createSubhandle("subhandle")
    }

    @OptIn(Purge::class)
    @AfterEach
    fun cleanup() {
        HandleManager.purge()
    }

    @Test
    fun `space test`() {
        assertSame(handle, space["handle"])
        assertSame(subhandle, space["handle:subhandle"])
    }

    @Test
    fun `handle test`() {
        assertSame(subhandle, handle["subhandle"])
        assertSame(subhandle, handle["handle:subhandle"])
    }

    @Test
    fun `group test`() {
        val group = space.createGroup("group")
        assertEquals(0, group.handles.size)
        assertFalse(handle in group.handles)
        assertFalse(subhandle in group.handles)
        group += handle
        assertEquals(1, group.handles.size)
        assertTrue(handle in group.handles)
        assertFalse(subhandle in group.handles)
        group += subhandle
        assertEquals(2, group.handles.size)
        assertTrue(handle in group.handles)
        assertTrue(subhandle in group.handles)
        assertThrows<IllegalArgumentException> { group += space.handle }
    }

    @Test
    fun `handle set test`() {
        val set = HandleSet(elements = listOf(handle))
        assertEquals(1, set.size)
        assertTrue(handle in set)
        assertFalse(subhandle in set)
        val immutable = set.immutableView
        assertTrue(handle in immutable)
        assertFalse(subhandle in immutable)
        set += subhandle
        assertEquals(2, set.size)
        assertTrue(handle in set)
        assertTrue(subhandle in set)
        assertTrue(handle in immutable)
        assertTrue(subhandle in immutable)
        set += "hello"
        val hello = space["hello"]!!
        assertEquals(3, set.size)
        assertTrue("hello" in set)
        assertTrue("hello" in immutable)
        val tag1 = HandleManager.tagSpace.createHandle("tag1")
        val tag2 = HandleManager.tagSpace.createHandle("tag2")
        handle += tag1
        subhandle += listOf(tag1, tag2)
        hello += listOf("tag2", "tag3")
        assertEquals(setOf(handle, subhandle), set.getByTag(tag1))
        assertEquals(setOf(subhandle, hello), set.getByTag("tag2"))
        set -= listOf("handle", "handle:subhandle")
        assertEquals(1, set.size)
        assertFalse(handle in set)
        assertFalse(subhandle in set)
        assertFalse(handle in immutable)
        assertFalse(subhandle in immutable)
        assertThrows<IllegalArgumentException> { set += space.handle }
    }
}