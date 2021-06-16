package kweb.demos.helloWorld

import kweb.ElementCreator
import kweb.WebBrowser
import java.math.BigInteger
import java.security.SecureRandom
import java.time.Duration
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec



data class User(
    val id: Int?,
    val username: String,
    val passwordHash: String,
    val sessionId: String?=null
)

fun ElementCreator<*>.authRequired(block: ElementCreator<*>.()->Unit) {
    if(this.browser.authenticatedUser!=null){
        block()
    }
    else {
        if(this.browser.url.value != "/") {
            this.browser.navigateTo("/login?next=${this.browser.url.value}")
        }
        else {
            this.browser.navigateTo("/login")
        }
    }
}

fun WebBrowser.navigateTo(path:String){
    this.callJsFunction("window.location = \"${path}\"")
}

fun WebBrowser.getOrCreateSessionId() : String? {
    val sessionCookie = this.httpRequestInfo.cookies.get("SESSION")
    if(sessionCookie == null){
        val sessionId = UUID.randomUUID().toString()
        doc.cookie.set("SESSION", sessionId, expires = Duration.ofDays(14))
        logger.info("SessionID ${sessionId}")
        return sessionId
    }
    logger.info("SessionID ${sessionCookie}")
    return sessionCookie
}

val WebBrowser.authenticatedUser : User?
    get() = getOrCreateSessionId()?.let{Sessions.get(it)}

fun WebBrowser.clearSession() {
    val sessionId = getOrCreateSessionId()
    doc.cookie.set("SESSION", UUID.randomUUID().toString(), expires = Duration.ofDays(14))
    sessionId?.let{Sessions.remove( sessionId)}

}

object Sessions {

    fun start(sessionId: String, user: User){
        openSessions[sessionId] = user
        UserDatabase.save(user.copy(sessionId = sessionId))
    }

    fun get(sessionId: String) : User? {
        if(openSessions[sessionId]!=null)
            return openSessions[sessionId]
        val user = UserDatabase.findUserBySessionId(sessionId)

        if(user!=null){
            openSessions[sessionId] = user
        }

        return user
    }

    fun remove(sessionId: String){
        this.openSessions.remove(sessionId)

        UserDatabase.findUserBySessionId(sessionId)?.let { user->
            UserDatabase.save(user.copy(sessionId = null))
        }

    }
    private val openSessions = mutableMapOf<String, User>()
}

object UserDatabase {
    private val users = mutableListOf(
        User(2, "frnk", generatePasswordHash("youWillNeverGuessIt\uD83D\uDE08")),
        User(1,"sanity", generatePasswordHash("kweb")),
        User(3,"june", generatePasswordHash("niceMonth")),
        User(4, "test", generatePasswordHash("test"))
    )

    fun findUserBySessionId(sessionId: String):User? = users.find { it.sessionId == sessionId }
    fun save(user: User){
        if(user.id == null){
            users.add(user.copy(id = users.size))
        }else {
            val idx = users.indexOfFirst { it.id == user.id }
            users[idx] = user
        }
    }
    fun checkLoginAndReturnUser(username:String, pass: String): User?{
        val user = users.find { it.username == username }
        if(checkPasswordHash(user?.passwordHash, pass)){
            return user
        }
        return null
    }
}

fun checkPasswordHash(currentPasswordHash:String?, password:String):Boolean{
    currentPasswordHash ?: return false

    val (format, iterations, salt, hash) = currentPasswordHash.split(":")

    val actualHash = generatePasswordHash(password, salt, iterations.toInt())

    return actualHash == currentPasswordHash
}

fun String.hexStringToByteArray(): ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4)
                + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun toHex(array: ByteArray): String {
    val bi = BigInteger(1, array)
    val hex = bi.toString(16)
    val paddingLength = array.size * 2 - hex.length
    return if (paddingLength > 0) {
        String.format("%0" + paddingLength + "d", 0) + hex
    } else {
        hex
    }
}

fun generatePasswordHash(password: String, initialSalt:String? = null, iterations: Int = 1000): String {
    val chars = password.toCharArray()
    val salt = initialSalt?.hexStringToByteArray() ?: getSalt()
    val spec = PBEKeySpec(chars, salt, iterations, 64 * 8)
    val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val hash = skf.generateSecret(spec).encoded
    return "pbkdf2:"+iterations.toString() + ":" + toHex(salt) + ":" + toHex(hash)
}

fun getSalt(): ByteArray {
    val sr: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
    val salt = ByteArray(16)
    sr.nextBytes(salt)
    return salt
}