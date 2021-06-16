package kweb.demos.helloWorld.pages

import kweb.ElementCreator
import kweb.h4

fun ElementCreator<*>.publicPage(){
    h4().text("Everyone can visit this page. No auth requrired...")
}