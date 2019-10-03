import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VsTest {

    @Test
    fun test3() {
        runBlocking {
            launch {
                println("a")
                val actions = Channel<Int>()
                println("b")
                launch {
                    getSearchResults(actions)
                    actions.close()
                }
                println("c")
                for (newAction in actions) { println(newAction) }
                println("d")
            }
        }
    }

    private suspend fun getSearchResults(actions: Channel<Int>) = coroutineScope {
        println("1")

        actions.send(1)
        println("2")

        val uploadProgress = Channel<Int>(Channel.CONFLATED)
        println("3")

        launch {
            for (step in uploadProgress) {
                actions.send(step)
            }
        }

        try {
            for (i in 10..20) {
                uploadProgress.send(i)
                delay(500)
            }
            actions.send(1000)
        } finally {
            uploadProgress.close()
        }

        println("4")
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Test
    fun test1() {

        runBlocking {

            println("ala")

            val rec = produce<Int> {
                send(2)
            }

            try {
                val con = CoroutineScope(coroutineContext)
                con.launch {

                }

                coroutineScope {
                    launch {
                        delay(2000)
                        println("zupa")
                        if (System.currentTimeMillis() > 0) throw AssertionError("aaa")
                    }
                }
            } catch (error: Throwable) {
                println("bum")
            }

            rec.consumeEach {
                println("-> $it")
            }

            println("ola")
        }
    }

    @ObsoleteCoroutinesApi
    @ExperimentalCoroutinesApi
    @Test
    fun test2() {

        runBlocking {

            println("ala")

            val value = suspendCoroutine<Int> { continuation ->
                launch(Dispatchers.Default) {
                    delay(2000)
                    continuation.resume(44)
                }
            }

            println("ola: $value")
        }
    }
}
