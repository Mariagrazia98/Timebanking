package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class OtherMessageRecyclerViewAdapter(val data: Message) : RecyclerView.Adapter<OtherMessageRecyclerViewAdapter.OtherMessageViewHolder>() {

    var message = data

    class OtherMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val messageText: TextView = v.findViewById(R.id.text_gchat_user_other)
        val timeText: TextView = v.findViewById(R.id.text_gchat_timestamp_other)
        val nameText: TextView = v.findViewById(R.id.text_gchat_message_other)

        fun bind(item: Message) {
            messageText.text = item.message
            timeText.text = item.createdAt.toString()
            nameText.text = item.sender.nickname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherMessageViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(it.polito.timebanking.R.layout.fragment_other_message, parent, false)
        return OtherMessageViewHolder(vg)
    }

    override fun onBindViewHolder(holder: OtherMessageViewHolder, position: Int) {
        val item = message
        item.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = 1

}