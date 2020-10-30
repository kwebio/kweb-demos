package kweb.demos.helloWorld

import kweb.*
import kweb.plugins.fomanticUI.fomantic
import kweb.plugins.fomanticUI.fomanticUIPlugin
import kweb.state.KVar
import kweb.state.render

fun main(args: Array<String>) {
    formDemo()
}

fun formDemo() {
    Kweb(port = 16097, plugins = listOf(fomanticUIPlugin)) {
        doc.body.new {
            centeredBox {
                h1().text("Form Demo")

                val successMessage = KVar<String?>(null)


                val firstName = KVar("")
                val agreed = KVar(false)
                val bike = KVar("A")
                val color = KVar<String?>(null)

                formControl { form->
                    formInput("First Name", "Jane", true, firstName)
                            .validate { input->
                                when{
                                    input == null -> "Please enter a name!"
                                    input.length < 3 -> "Please enter a longer name!. It shall have at least three chars."
                                    else -> null
                                }
                            }
                            .with(form)

                    checkBoxInput("Please agree to nothing", agreed)

                    radioInput("Which bicycle do you ride?", mapOf("Racing Bike" to "RB", "Mountain Bike" to "MTB", "Dutch Bike" to "FIETS", "None" to "X"), false, false, bike)

                    dropdownField("Color", mapOf("red" to "Red", "yellow" to "Yellow", "green" to "Green"), color)

                    render(form.errors){foundErrors->
                        ul().new {
                            foundErrors.forEach { error->
                                li().text(error)
                            }
                        }
                    }

                    render(successMessage){msg ->
                        if(msg != null) {
                            p().new{
                                i(fomantic.icon.check)
                                span().text(msg)
                            }
                        }
                    }


                    button(fomantic.ui.button).text("Submit").on.click {
                        if(form.isValid){
                            logger.info("Input is valid!")
                            successMessage.value = "Greetings ${firstName.value}. Will you agree? ${agreed.value}. You selected option ${bike.value}. Selected color: ${color.value}"
                        }
                        else {
                            logger.error("Input is not valid!")
                        }
                    }
                }
            }
        }
    }
}