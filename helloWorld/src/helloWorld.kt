package kweb.demos.helloWorld

import kweb.*

fun main(args: Array<String>) {
    helloWorld()
}

fun helloWorld() {
    Kweb(port = 16097) {
        doc.body.new {
            h1().text("Hello Worlds!")
            ul().new {
                for (x in 1..5) {
                    li().text("Hello World $x!")
                }
            }
        }
    }
}