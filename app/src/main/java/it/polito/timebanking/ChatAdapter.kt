package it.polito.timebanking

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.User
import java.text.SimpleDateFormat


class ChatAdapter(var data: List<ChatMessage>?, val userSenderId: String, val userOfferer: User) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder<*>>() {

    var list = data!!.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context = parent.context

        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.fragment_sender_message, parent, false)
                SentMessageViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_receiver_message, parent, false)
                ReceivedMessageViewHolder(view, userOfferer)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder<*>, position: Int) {
        val item = list[position]

        setViewType(position)

        when (holder) {
            is SentMessageViewHolder -> holder.bind(item)
            is ReceivedMessageViewHolder -> holder.bind(item)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = list.size


    fun setViewType(position: Int){
        if(position > 0 && list[position].date != list[position-1].date)
            list[position].type = 1
        else if(position==0)
            list[position].type = 1
        else
            list[position].type = 0
    }

    override fun getItemViewType(position: Int): Int {
        if(list[position].idSender == userSenderId)
            return 0
        else
            return 1
    }

    abstract class MessageViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    class SentMessageViewHolder(val view: View) : MessageViewHolder<ChatMessage>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.text_gchat_message_me)
        private val msgTime = view.findViewById<TextView>(R.id.text_gchat_timestamp_me)
        private val dateView = view.findViewById<TextView>(R.id.text_gchat_date_me)

        override fun bind(item: ChatMessage) {
            if(item.type == 1){
                dateView.visibility = View.VISIBLE

                dateView.text = item.date
            }
            messageContent.text = item.text
            val tlist = item.time.split(":")
            val t = tlist[0] + ":" + tlist[1]
            msgTime.text = t
        }
    }

    class ReceivedMessageViewHolder(val view: View, val user: User) : MessageViewHolder<ChatMessage>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.text_gchat_message_other)
        val profileNameChatView = view.findViewById<TextView>(R.id.text_gchat_user_other)
        val profileImageView = view.findViewById<ImageView>(R.id.image_gchat_profile_other)
        private val msgTime = view.findViewById<TextView>(R.id.text_gchat_timestamp_other)
        private val dateView = view.findViewById<TextView>(R.id.text_gchat_date_other)

        override fun bind(item: ChatMessage) {
            if(item.type == 1){
                dateView.visibility = View.VISIBLE
                dateView.text = item.date
            }
            messageContent.text = item.text
            profileNameChatView.text = user.fullname
            val tlist = item.time.split(":")
            val t = tlist[0] + ":" + tlist[1]
            msgTime.text = t
            // Download directly from StorageReference using Glide
            if(user.imagePath!=null)
                Glide.with(profileImageView.context).load(user.imagePath).into(profileImageView)

            profileImageView.setOnClickListener{
                val bundle = bundleOf("userId" to (user.uid), "read_only" to true)
                NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_chatFragment_to_showProfileFragment, bundle)
            }
            profileNameChatView.setOnClickListener{
                val bundle = bundleOf("userId" to (user.uid), "read_only" to true)
                NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_chatFragment_to_showProfileFragment, bundle)
            }
        }

    }

}