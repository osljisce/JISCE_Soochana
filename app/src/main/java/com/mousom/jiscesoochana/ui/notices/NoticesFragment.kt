package com.mousom.jiscesoochana.ui.notices

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mousom.jiscesoochana.R
import com.mousom.jiscesoochana.data.adapter.NoticeAdapter
import com.mousom.jiscesoochana.databinding.FragmentNoticesBinding
import com.mousom.jiscesoochana.repository.BaseRepository
import com.mousom.jiscesoochana.utils.ConnectionCheckUtil
import com.mousom.jiscesoochana.utils.Constants.Companion.BASE_URL
import com.mousom.jiscesoochana.utils.DownloadNotice.downloadCurrentNotice
import org.jsoup.Jsoup


class NoticesFragment : Fragment() {

    private var _binding: FragmentNoticesBinding? = null
    private lateinit var noticeUrlToDownload: String
    private lateinit var connectionCheckUtil: ConnectionCheckUtil
    private var isActiveConnection: Boolean = false
    private val noticeTitleList: MutableList<String> = mutableListOf()
    private val noticeLinkList: MutableList<String> = mutableListOf()
    private val noticeDateList: MutableList<String> = mutableListOf()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNoticesBinding.inflate(inflater, container, false)

        val root: View = binding.root

        val repository = BaseRepository()
        val viewModelFactory = NoticesViewModelFactory(repository)
        val noticesViewModel =
            ViewModelProvider(this, viewModelFactory)[NoticesViewModel::class.java]

        val recyclerview = binding.recyclerNoticeView
        connectionCheckUtil = ConnectionCheckUtil(application = requireActivity().application)

        connectionCheckUtil.observe(viewLifecycleOwner) { isActive ->
            isActiveConnection = isActive
            if (isActive) {
                noticesViewModel.getNoticeData("/notice-board.php")
            } else {
                Snackbar.make(binding.root, "No Internet Connection 😪", Snackbar.LENGTH_LONG)
                    .show()
            }

        }


        noticesViewModel.getNoticeDataResponse.observe(requireActivity()) { response ->
            if (response.isSuccessful && isActiveConnection) {
                fetchAndShowNoticesToView(response.body().toString())
                val adapter = NoticeAdapter(noticeTitleList, noticeLinkList, noticeDateList)
//                Log.d("Value", adapter.itemCount.toString())
                recyclerview.adapter = adapter

                adapter.setOnItemClickListener(object : NoticeAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Do you want to download the notice?")
                            .setMessage(noticeTitleList[position])
                            .setNegativeButton("No") { dialog, which ->
                            }
                            .setPositiveButton("Yes") { dialog, which ->
                                downloadCurrentNotice(
                                    Uri.parse("$BASE_URL/${noticeLinkList[position]}"),
                                    requireActivity()
                                )
                            }
                            .show()
                    }

                })

            }

        }


        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        return root
    }

    private fun fetchAndShowNoticesToView(response: String) {
        val htmlContent = Jsoup.parse(response)
        val noticeContent = htmlContent.select("div.timeline-left > div.item > p > a")
        val noticeTitle = htmlContent.select("div.timeline-left > div.item > p")
        val noticeDate = htmlContent.select("div.timeline-left > div.item > h4")
        val latestNotice = noticeContent.attr("href")
        val noticeUrl = "$BASE_URL/$latestNotice"
        noticeUrlToDownload = noticeUrl
        noticeTitle.forEach { title ->
            noticeTitleList.add(title.text())
        }
        noticeDate.forEach { date ->
            noticeDateList.add(date.text())
        }
        noticeContent.forEach { link ->
            noticeLinkList.add(link.attr("href"))
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}