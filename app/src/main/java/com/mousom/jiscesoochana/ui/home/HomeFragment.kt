package com.mousom.jiscesoochana.ui.home

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.android.material.snackbar.Snackbar
import com.mousom.jiscesoochana.databinding.FragmentHomeBinding
import com.mousom.jiscesoochana.repository.BaseRepository
import com.mousom.jiscesoochana.utils.ConnectionCheckUtil
import com.mousom.jiscesoochana.utils.Constants.Companion.BASE_URL
import org.jsoup.Jsoup


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var webView: WebView
    private lateinit var noticeUrlToDownload: String
    private lateinit var connectionCheckUtil: ConnectionCheckUtil
    private var isActiveConnection: Boolean = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val repository = BaseRepository()
        val viewModelFactory = HomeViewModelFactory(repository)
        val homeViewModel =
            ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        webView = binding.noticeView
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        val settings = webView.settings
        connectionCheckUtil = ConnectionCheckUtil(application = requireActivity().application)

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

        connectionCheckUtil.observe(viewLifecycleOwner) { isActive ->
            isActiveConnection = isActive
            if (isActive) {
                homeViewModel.getNoticeData("/")
            } else {
                Snackbar.make(binding.root, "No Internet Connection 😪", Snackbar.LENGTH_LONG)
                    .show()
            }

        }


        homeViewModel.getNoticeDataResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful && isActiveConnection) {
                fetchAndShowNoticeToView(response = response.body().toString())

                webView.webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        binding.floatingActionButton.visibility = View.VISIBLE

                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        webView.visibility = View.VISIBLE
                        binding.loadNotice.visibility = View.GONE

                    }

                }

            }

        }

        binding.floatingActionButton.setOnClickListener {
            webView.reload()
            binding.loadNotice.visibility = View.VISIBLE
        }
        return root
    }

    private fun fetchAndShowNoticeToView(response: String) {
        val htmlContent = Jsoup.parse(response)
        val noticeContent = htmlContent.select("li.tab-post > a").first()
        val latestNotice = noticeContent?.attr("href")
        val noticeUrl = "$BASE_URL/$latestNotice"
        noticeUrlToDownload = noticeUrl
        webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=$noticeUrl")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        webView.destroy()
        _binding = null
    }
}