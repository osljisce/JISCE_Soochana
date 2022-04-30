package com.mousom.jiscesoochana.ui.result

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.snackbar.Snackbar
import com.mousom.jiscesoochana.databinding.FragmentResultBinding
import com.mousom.jiscesoochana.utils.ConnectionCheckUtil

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private lateinit var webView: WebView
    private lateinit var connectionCheckUtil: ConnectionCheckUtil

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ResultViewModel::class.java)

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val root: View = binding.root
        connectionCheckUtil = ConnectionCheckUtil(application = requireActivity().application)
        webView = binding.resultView
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        val settings = webView.settings


        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {

            when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_ON)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    WebSettingsCompat.setForceDark(settings, WebSettingsCompat.FORCE_DARK_OFF)
                }

            }

        }

        connectionCheckUtil.observe(viewLifecycleOwner){ isActive->

            if(isActive){
                webView.loadUrl("http://jisexams.in/JISEXAMS/StudentServices/frmViewStudentGradeCardResult.aspx")
            }else{
                Snackbar.make(binding.root, "No Internet Connection 😪", Snackbar.LENGTH_LONG)
                    .show()
            }

        }



        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.resultView.visibility = View.VISIBLE

            }

            override fun onPageFinished(view: WebView, url: String) {
                webView.visibility = View.VISIBLE
                binding.progressResult.visibility = View.GONE

            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                webView.loadUrl("https://latifulmousom.github.io/JISCE-Soochana/")

                if (error != null) {
                    Snackbar.make(
                        root,
                        "Something Went Wrong 😐",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Retry") {
                            webView.reload()
                        }
                        .show()
                }
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webView.destroy()
        _binding = null
    }
}