package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageListRecyclerViewAdapter(val data: List<Message>) : RecyclerView.Adapter<MessageListRecyclerViewAdapter.MessageListViewHolder>() {
    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    var list = data.toMutableList()


    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        val message: Message = list.get(position)

        return if (message.sender.uid.equals(1)) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    class MessageListViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bind(item: Message) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageListViewHolder {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_my_message, parent, false)
            return MessageListViewHolder(view)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_other_message, parent, false)
            return MessageListViewHolder(view)
        }

        //rivedere
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_other_message, parent, false)
        return MessageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageListViewHolder, position: Int) {
        val item = list[position]
        item.let {
            when (holder.getItemViewType()) {
                VIEW_TYPE_MESSAGE_SENT -> MyMessageRecyclerViewAdapter(item)
                VIEW_TYPE_MESSAGE_RECEIVED -> OtherMessageRecyclerViewAdapter(item)
                else -> OtherMessageRecyclerViewAdapter(item) //rivedere
            }
        }
    }

    override fun getItemCount(): Int = list.size

}