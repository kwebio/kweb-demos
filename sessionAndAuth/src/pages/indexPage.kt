package kweb.demos.helloWorld.pages

import kweb.ElementCreator
import kweb.a
import kweb.br
import kweb.h4

fun ElementCreator<*>.indexPage(){
    h4().text("Welcome. Where do you want to go?")
    a(href = "/publicPage").text("Go to Public area")
    br()
    a(href = "/privatePageWhereLoginIsRequried").text("Go to Private area. Login required")
}