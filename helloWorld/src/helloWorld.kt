package kweb.demos.helloWorld

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kweb.*
import java.lang.Exception
import java.util.concurrent.CompletableFuture
import kotlin.random.Random

fun main(args: Array<String>) {
    helloWorld()
}

fun helloWorld() {
    Kweb(port = 16097) {
        doc.body.new {
            h1().text("Hello Worlds!")

            GlobalScope.launch {
                try {
                    var x = 0
                    while (true) {
                        x += 1
                        li().text("Hello World $x!")


                        val callbackId = Random.nextInt()


                        withTimeout(2000){
                            val waiter = CompletableFuture<Boolean>()
                            GlobalScope.launch(Dispatchers.IO){
                                browser.executeWithCallback("callbackWs($callbackId, {alive: 1});", callbackId) {
                                    waiter.complete(true)
                                }
                            }
                            waiter.await()
                        }

                        delay(500)
                    }
                }
                catch (ex:Exception){
                    logger.info(ex.message)
                }
                finally {
                    logger.info("I'm gone")
                }
            }
        }
    }
}