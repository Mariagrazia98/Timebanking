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
import it.polito.timebanking.MessageItemUi.Companion.TYPE_FRIEND_MESSAGE
import it.polito.timebanking.MessageItemUi.Companion.TYPE_MY_MESSAGE
import it.polito.timebanking.model.User


class ChatAdapter(var data: MutableList<MessageItemUi>, val user: User) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder<*>>() {

    var list = data.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context = parent.context

        return when (viewType) {
            TYPE_MY_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.fragment_sender_message, parent, false)
                SentMessageViewHolder(view)
            }
            TYPE_FRIEND_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_receiver_message, parent, false)
                ReceivedMessageViewHolder(view, user)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder<*>, position: Int) {
        val item = data[position]
        Log.d("adapter View", position.toString() + item.content)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(item)
            is ReceivedMessageViewHolder -> holder.bind(item)
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemCount(): Int = data.size



    override fun getItemViewType(position: Int): Int = data[position].messageType

    abstract class MessageViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    class SentMessageViewHolder(val view: View) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.text_gchat_message_me)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content

        }
    }

    class ReceivedMessageViewHolder(val view: View, val user: User) : MessageViewHolder<MessageItemUi>(view) {
        private val messageContent = view.findViewById<TextView>(R.id.text_gchat_message_other)
        val profileNameChatView = view.findViewById<TextView>(R.id.text_gchat_user_other)
        val profileImageView = view.findViewById<ImageView>(R.id.image_gchat_profile_other)

        override fun bind(item: MessageItemUi) {
            messageContent.text = item.content
            profileNameChatView.text = user.fullname
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