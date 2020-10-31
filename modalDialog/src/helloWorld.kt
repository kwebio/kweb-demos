package kweb.demos.helloWorld

import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.state.KVar
import kweb.state.render

fun main(args: Array<String>) {
    helloWorld()
}

fun ElementCreator<*>.doSomethingModal(whenDone:(input:String)->Unit) = modal("Enter a number"){modal->
    val userInput = KVar("")

    form(fomantic.ui.form).new {
        div(fomantic.ui.field).new {
            label().text("Input a number")
            input(InputType.text, placeholder = "111").apply { value=userInput }
        }

        button(fomantic.ui.button).text("Save").on.click {
            whenDone(userInput.value)
            modal.close()
        }
    }
}

fun helloWorld() {
    Kweb(port = 16097, plugins = listOf(fomanticUIPlugin)) {

        val modalResult = KVar("")

        doc.body.new {
            div(fomantic.ui.main.container).new {

                div(fomantic.column).new {
                    div(fomantic.ui.vertical.segment).new {

                        h1(fomantic.ui.header).text("Modal demo")
                        div(fomantic.ui.content).new {
                            val modal = doSomethingModal { userInput->
                                logger.info("User entered ${userInput}")
                                modalResult.value = userInput
                            }

                            button(fomantic.ui.button).text("Modal 1").on.click {
                                modal.open()
                            }

                            div(fomantic.ui.divider.hidden)

                            render(modalResult){result->
                                p().text("User choice is $result")
                            }
                        }
                    }
                }
            }
        }
    }
}