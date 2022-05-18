package it.polito.timebanking

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.MessageItemUi.Companion.TYPE_FRIEND_MESSAGE
import it.polito.timebanking.MessageItemUi.Companion.TYPE_MY_MESSAGE


class ChatAdapter(var data: MutableList<MessageItemUi>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder<*>>() {

    var list = data.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context = parent.context

        return when (viewType) {
            TYPE_MY_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.fragment_sender_message, parent, false)
                MyMessageViewHolder(view)
            }
            TYPE_FRIEND_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_receiver_message, parent, false)
                FriendMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder<*>, position: Int) {
        val item = data[position]
        Log.d("adapter View", position.toString() + item.content)
        when (holder) {
            is MyMessageViewHolder -> holder.bind(item)
            is FriendMessageViewHolder -> holder.bind(item)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = data.size



    override fun getItemViewType(position: Int): Int = data[position].messageType

    abstract class MessageViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    class MyMessageViewHolder(val view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.text_gchat_message_me)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content
            //messageContent.textColor= item.textColor
        }
    }

    class FriendMessageViewHolder(val view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.text_gchat_message_other)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content
            //messageContent.textColor= item.textColor
        }
    }


}