package com.example.goodhomes.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.example.goodhomes.R

class WebViewFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.webview, container, false)


        val webView = view.findViewById<WebView>(R.id.webView) // Get the web view from xml
        webView.loadUrl("http://10.128.29.135/goodhomes/auth/login.php") // Set url for the web view
        webView.settings.javaScriptEnabled = true // Enable JavaScript (raises potential for xss)

        return view
    }
}