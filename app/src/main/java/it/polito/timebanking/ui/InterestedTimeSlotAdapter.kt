package it.polito.timebanking.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.timebanking.R
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User


class InterestedTimeSlotAdapter(
    val data: Map<User, List<TimeSlot>>,
    val status: String,
    val ownerUser: User?
) :

    RecyclerView.Adapter<InterestedTimeSlotAdapter.ItemSlotViewHolder>() {
    var list = data.toMutableMap().values.flatten()
    class ItemSlotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val title: TextView = v.findViewById(R.id.slot_title)
        private val date: TextView = v.findViewById(R.id.slot_date)
        private val time: TextView = v.findViewById(R.id.slot_time)
        private val duration: TextView = v.findViewById(R.id.slot_duration)
        private val name: TextView? = v.findViewById(R.id.offererName)
        val cv: CardView = v.findViewById(R.id.cvLastRating)
        val button: ImageButton? = v.findViewById(R.id.button)
        val ivSlot: ImageView? = v.findViewById(R.id.imageViewSlot)
        val chatButton: ImageButton? = v.findViewById(R.id.chatButton)
        val imageView : CircleImageView? = v.findViewById(R.id.imageViewSlot)


        fun bind(item: TimeSlot, user: User, status:String) {
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            if (ivSlot != null) {
                Glide.with(ivSlot.context).load(user.imagePath).into(ivSlot)
            }

            if(status == "accepted"){
                val assignedTo = "Assigned to: " + user.fullname
                name?.isSingleLine = false
                name?.text = assignedTo

                imageView?.visibility = View.GONE
            }else{
                name?.text = user.fullname
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlotViewHolder {
        val vg: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_other_time_slot, parent, false)
        return ItemSlotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemSlotViewHolder, position: Int) {
        val item = list[position]
        lateinit var user: User
        data.toMutableMap().keys.forEach {
            if (data.toMutableMap()[it]?.contains(item) == true) {
                user = it
            }
        }
        item.let { holder.bind(it, user, status) }

        val bundle = bundleOf("read_only" to true)
        if(status == "interested" || status == "assigned")
            bundle.putBoolean( "mychats", false)
        else if(status== "accepted")
            bundle.putBoolean( "mychats", true)
        bundle.putSerializable("slot", item)

        holder.cv.setOnClickListener {
            if(status == "assigned" || status == "interested"){
                bundle.putSerializable("user", user)
            }else {
                bundle.putSerializable("user", ownerUser)
            }
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
                bundle
            )
        }

        holder.chatButton?.setOnClickListener {
            bundle.putSerializable("user", user)
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_chatFragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int = list.size
}