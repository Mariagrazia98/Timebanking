package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.polito.timebanking.model.ChatUser
import it.polito.timebanking.model.TimeSlot


class ChatTitleAdapter(val data: List<ChatUser>, val slot: TimeSlot) : RecyclerView.Adapter<ChatTitleAdapter.ChatTitleViewHolder>() {

    var list = data.toMutableList()

    class ChatTitleViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cv: CardView = v.findViewById(R.id.chatTitleCv)
        val chatPersonName: TextView = v.findViewById(R.id.chatPersonName)
        val imageViewPerson: ImageView = v.findViewById(R.id.imageViewPerson)

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

        val bundle = bundleOf("mychats" to true)
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