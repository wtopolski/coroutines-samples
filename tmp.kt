import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Test
import java.lang.Exception

// https://proandroiddev.com/kotlin-coroutines-patterns-anti-patterns-f9d12984c68e
class VsCoroTest {

    @Test
    fun test2() {
        runBlocking {

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught: $exception")
            }
            GlobalScope.launch(handler) {
                throw AssertionError()
            }
//            val deferred = GlobalScope.async(handler) {
//                throw ArithmeticException() // Nothing will be printed, relying on user to call deferred.await()
//            }
//            joinAll(job, deferred)

//            job.join()
        }
    }

    @Test
    fun test1() {
        runBlocking {

            val handler = CoroutineExceptionHandler { _, exception ->
                println("Caught $exception")
            }

            val x = CoroutineScope(handler).launch {
                println("Start")

                val j = launch {
                    println("Throwing exception from launch")
                    throw AssertionError()
                }

                j.join()
                println("After A")
            }

            x.join()
            println("After B")
        }
    }

    /**
     * A
     * D
     * B
     * java.lang.ArithmeticException
     */
    @Test
    fun test5() {
        runBlocking {
            val job1 = launch {
                println("A")
                try {
                    launch {
                        println("B")
                        if (System.nanoTime() > 0) throw ArithmeticException()
                        println("C")
                    }
                } catch (error: Exception) {
                    println("Error: $error")
                }
                println("D")
            }

            job1.join()
            launch {
                println("E")
            }
        }
        println("F")
    }

    /**
     * A
     * D
     * B
     * Exception in thread "main @coroutine#3" java.lang.ArithmeticException
     * E
     * F
     */
    @Test
    fun test6() {
        runBlocking {
            val job1 = launch(SupervisorJob()) {
                println("A")
                try {
                    launch {
                        println("B")
                        if (System.nanoTime() > 0) throw ArithmeticException()
                        println("C")
                    }
                } catch (error: Exception) {
                    println("Error: $error")
                }
                println("D")
            }

            job1.join()
            launch {
                println("E")
            }
        }
        println("F")
    }

    /**
     * A
     * D
     * B
     * Exception in thread "main @coroutine#3" java.lang.ArithmeticException
     * E
     * F
     */
    @Test
    fun test7() {
        runBlocking {
            val job1 = supervisorScope {
                launch {
                    println("A")
                    try {
                        launch {
                            println("B")
                            if (System.nanoTime() > 0) throw ArithmeticException()
                            println("C")
                        }
                    } catch (error: Exception) {
                        println("Error: $error")
                    }
                    println("D")
                }
            }

            job1.join()
            launch {
                println("E")
            }
        }
        println("F")
    }

    /**
     * A
     * Ex: java.lang.ArithmeticException
     * B
     * C
     */
    @Test
    fun test3() {
        runBlocking {
            val deferred = supervisorScope {
                async {
                    delay(100)
                    if (System.nanoTime() > 0) throw ArithmeticException()
                }
            }
            println("A")

            try {
                deferred.await()
            } catch (exception: Exception) {
                println("Ex: $exception")
            }

            println("B")

            launch {
                println("C")
            }
        }
    }

    /**
     * A
     * Exception in thread "main @coroutine#2" java.lang.ArithmeticException
     * C
     */
    @Test
    fun test4() {
        runBlocking {
            val job1 = launch(SupervisorJob()) {
                println("A")
                async {
                    delay(100)
                    if (System.nanoTime() > 0) throw ArithmeticException()
                }.await()
                println("B")
            }

            job1.join()
            launch {
                println("C")
            }
        }
    }
    
    

    @Test
    fun aaa() {
        runBlocking {
            println("start")

            val j = launch {
                println("inner")
                kotlin.runCatching {
                    delay(500)

                    suspendCoroutine<Int> { con ->
                        con.resumeWithException(Exception("AAA"))
                    }

                }.onFailure { error ->
                    println("blad: " + error)
                }
            }

            j.join()
            println("koniec")
        }
    }

    @Test
    fun bbb() {
        runBlocking {
            println("start")

            try {
                coroutineScope {
                    println("start 1")
                    try {
                        launch {
                            throw Exception("Bum")
                        }
                    } catch (error: Exception) {
                        println("inner error: $error")
                    }
                    println("stop 1")
                }
            } catch (error: Exception) {
                println("inner error: $error")
            }

            println("koniec")
        }
    }
}
