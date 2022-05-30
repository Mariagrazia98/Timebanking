package it.polito.timebanking

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
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User


class InterestedTimeSlotRecyclerViewAdapter(
    val data: Map<User, List<TimeSlot>>,
    val status: String
) :

    RecyclerView.Adapter<InterestedTimeSlotRecyclerViewAdapter.ItemSlotViewHolder>() {
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


        fun bind(item: TimeSlot, user: User, status:String) {
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            if (ivSlot != null) {
                Glide.with(ivSlot.context).load(user.imagePath).into(ivSlot)
            }

            if(status == "accepted"){
                val assignedTo = "Assigned to:\n" + user.fullname
                name?.isSingleLine = false
                name?.text = assignedTo
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
        bundle.putSerializable("user", user)
        bundle.putSerializable("slot", item)

        holder.cv.setOnClickListener {
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
                bundle
            )
        }

        holder.button?.setOnClickListener {
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                bundle
            )
        }

        holder.chatButton?.setOnClickListener {
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_chatFragment,
                bundle
            )
        }
    }

    override fun getItemCount(): Int = list.size
}