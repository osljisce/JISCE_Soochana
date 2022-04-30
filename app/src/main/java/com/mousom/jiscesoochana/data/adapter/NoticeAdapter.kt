package com.mousom.jiscesoochana.data.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mousom.jiscesoochana.R


class NoticeAdapter(private val title: List<String>, private val link: List<String>, private val date: List<String>) :
    RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
private lateinit var mlistener: onItemClickListener
    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notice_view, parent, false)

        val holder = ViewHolder(view, mlistener)

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val noticeTitle = title[position]
        val noticeDate = date[position]

        holder.textView.text = noticeTitle
        holder.noticeDate.text = noticeDate

    }

    override fun getItemCount(): Int {
        return title.size
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.notice_title)
        val noticeDate: TextView = itemView.findViewById(R.id.notice_date)

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


}
