package kweb.demos.helloWorld.pages

import kotlinx.serialization.json.JsonPrimitive
import kweb.*
import kweb.demos.helloWorld.Sessions
import kweb.demos.helloWorld.UserDatabase
import kweb.demos.helloWorld.getOrCreateSessionId
import kweb.demos.helloWorld.navigateTo
import kweb.state.KVar
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("loginPage")


fun ElementCreator<*>.loginPage(){
    h4().text("Please authenticate")
    p().text("If you are unsure, log in using test / test as username and password")

    val username = KVar("")
    val password = KVar("")
    val error = KVar("")


    div(attributes = mapOf("style" to JsonPrimitive("color: red;"))).text(error)


    input(type = InputType.text,placeholder = "user").apply { this.value = username  }.on.focusin { error.value = "" }
    br()
    input(type = InputType.password,placeholder = "password").apply { this.value = password }.on.focusin { error.value = "" }
    br()
    button { text("Log in") }.on.click {

        logger.info("Logging in as ${username.value} with password ${"*".repeat(password.value.length)}")

        val user = UserDatabase.checkLoginAndReturnUser(username.value, password.value)
        if(user != null){
            Sessions.start(this.browser.getOrCreateSessionId()!!, user)
            this.browser.navigateTo("/")
        }
        else {
            error.value = "Check username and password"
        }
    }

}