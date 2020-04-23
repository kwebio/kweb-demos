package kweb.demo.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import kweb.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Kweb(port = 16097, buildPage = {
            doc.body.new {
                h1().text("Hello World!")
            }
        })

        val webView: WebView = findViewById(R.id.webViewWidget)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("http://localhost:16097")
    }
}
