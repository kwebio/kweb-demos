This project is an android app with embedded [kweb](https://github.com/kwebio/kweb-core) server. 
The server is running on port 16097. This app contains a webview which loads that page, but you 
can also load the page by navigating to http://localhost:16097 in your phone's browser.

# How to run Kweb in an android app

1) add the [Jitpack](https://jitpack.io/) repo to the 
    project's gradle file
    ([Jump to code](https://github.com/kwebio/kweb-demos/blob/master/android/build.gradle#L22)):
    `maven { url 'https://jitpack.io' }`
2) add the following config options to the `android` section of the app's gradle file
    ([jump to code](https://github.com/kwebio/kweb-demos/blob/master/android/app/build.gradle#L25)):
    ```groovy
        packagingOptions {
           exclude 'META-INF/*'
       }
    
       compileOptions {
           sourceCompatibility = 1.8
           targetCompatibility = 1.8
       }
    ``` 
3) add Kweb as a dependency ([jump to code](https://github.com/kwebio/kweb-demos/blob/master/android/app/build.gradle#L47)):
    ```groovy
    implementation('com.github.kwebio:core:0.7.0') {
        exclude(module: 'shoebox')
    }
    ```
    [Shoebox](https://github.com/kwebio/shoebox) must be excluded because it will prevent 
    the app from building 
    (error is `Space characters in SimpleName 'kweb/shoebox/samples/SamplesKt$basic usage 
    sample$usersByEmail$1' are not allowed prior to DEX version 040`)
4) Add the internet permission to the manifest
    ([jump to code](https://github.com/kwebio/kweb-demos/blob/master/android/app/src/main/AndroidManifest.xml#L4)): 
    `<uses-permission android:name="android.permission.INTERNET"/>`
5) Run the Kweb server in your main activity
    ([jump to code](https://github.com/kwebio/kweb-demos/blob/master/android/app/src/main/java/kweb/demo/android/MainActivity.kt#L15)):
    ```kotlin
    Kweb(port = 16097, buildPage = {
        doc.body.new {
            h1().text("Hello World!")
        }
    })
    ```

That's all you need in order for kweb to be running inside your app. If you want to also
embed the webview, you will need to:

1) Add a [WebView](https://developer.android.com/reference/android/webkit/WebView) somewhere 
    in your app, and have it load the server's url:
    ```kotlin
    val webView: WebView = findViewById(R.id.webViewWidget)
    
    webView.settings.javaScriptEnabled = true
    webView.webViewClient = WebViewClient()
    webView.loadUrl("http://localhost:16097")
    ```
2) allow the app to use cleartext http by adding this attribute to the `<application>` 
    tag in the manifest: `android:usesCleartextTraffic="true"`


Thanks to the following sources:
- [Diamantidis Guide to running Ktor on Android](https://diamantidis.github.io/2019/11/10/running-an-http-server-on-an-android-app)

# Caveats

This app has been tested against SDK version 26 (Android 8.0). However, according to 
[some sources](https://caniuse.com/#feat=websockets), websockets are supported in Android 4.4 and higher.
YMMV.