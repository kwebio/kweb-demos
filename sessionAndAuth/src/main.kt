package kweb.demos.helloWorld

import kweb.*
import kweb.demos.helloWorld.pages.*
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    sessionAndAuthDemo()
}

val logger = LoggerFactory.getLogger("auth")


fun sessionAndAuthDemo() {
    Kweb(port = 16097) {
        doc.body.new {
            route {

                path("/login"){
                    loginPage()
                }

                path("/publicPage"){
                    publicPage()
                }

                path("/privatePageWhereLoginIsRequried"){
                    privatePageWhereLoginIsRequired()
                }

                path("/logout"){
                    logoutPage()
                }

                path("/"){
                    indexPage()
                }

            }


        }
    }
}