package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyMessageRecyclerViewAdapter(val data: Message) : RecyclerView.Adapter<MyMessageRecyclerViewAdapter.MyMessageViewHolder>() {

    var message = data

    class MyMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val messageText: TextView = v.findViewById(R.id.text_gchat_message_me)
        val timeText: TextView = v.findViewById(R.id.text_gchat_timestamp_me)

        fun bind(item: Message) {
            messageText.text = item.message
            timeText.text = item.createdAt.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(it.polito.timebanking.R.layout.fragment_my_message, parent, false)
        return MyMessageViewHolder(vg)
    }

    override fun onBindViewHolder(holder: MyMessageViewHolder, position: Int) {
        val item = message
        item.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = 1

}