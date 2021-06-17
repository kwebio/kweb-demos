package kweb.demos.helloWorld.pages

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kweb.ElementCreator
import kweb.demos.helloWorld.clearSession
import kweb.demos.helloWorld.navigateTo
import kweb.h3
import kweb.p

fun ElementCreator<*>.logoutPage(){
    h3().text("Logout")
    p().text("You were logged out. Redirect in 5 sek...")

    browser.clearSession()

    GlobalScope.launch {
        delay(5000)
        browser.navigateTo("/")
    }
}