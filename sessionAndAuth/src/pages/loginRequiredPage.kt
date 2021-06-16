package kweb.demos.helloWorld.pages

import kweb.ElementCreator
import kweb.a
import kweb.demos.helloWorld.authRequired
import kweb.demos.helloWorld.authenticatedUser
import kweb.h4
import kweb.p

fun ElementCreator<*>.privatePageWhereLoginIsRequired(){
    authRequired {
        h4().text("Member area")
        p().text("Hello ${this.browser.authenticatedUser?.username}")
        p().text("This area is only available after logging in.")
        a(href = "/logout").text("Log out")
    }
}