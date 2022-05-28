package it.polito.timebanking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.polito.timebanking.model.Chat
import it.polito.timebanking.model.ChatUser
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel


class ChatTitleAdapter(val data: List<ChatUser>, slot: TimeSlot) : RecyclerView.Adapter<ChatTitleAdapter.ChatTitleViewHolder>() {

    var list = data.toMutableList()
    val slot: TimeSlot = slot

    class ChatTitleViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cv: CardView = v.findViewById(R.id.chatTitleCv)
        val chatPersonName: TextView = v.findViewById(R.id.chatPersonName)
        val imageViewPerson: ImageView = v.findViewById(R.id.imageViewPerson)

        //profileVM.getUserById(item.reciverUid)

        fun bind(item: ChatUser) {
            chatPersonName.text = item.otherUser.fullname
            if(item.otherUser.imagePath!=null)
                Glide.with(imageViewPerson.context /* context */).load(item.otherUser.imagePath).into(imageViewPerson)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatTitleViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_chat_title_adapter, parent, false)
        return ChatTitleViewHolder(vg)
    }


    override fun onBindViewHolder(holder: ChatTitleViewHolder, position: Int) {
        val item = list[position]
        item.let { holder.bind(it) }

        val read_only = true
        val bundle = bundleOf("read_only" to read_only, "skill" to item)
        bundle.putSerializable("user", item.otherUser)
        bundle.putSerializable("slot", slot)
        holder.cv.setOnClickListener {
            NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_chatListFragment_to_chatFragment,
                bundle
            )
        }

    }

    override fun getItemCount(): Int = list.size

}