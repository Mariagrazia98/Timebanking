package it.polito.timebanking

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import it.polito.timebanking.model.TimeSlotFire


class MyTimeSlotRecyclerViewAdapter(val data: List<TimeSlotFire>, userId: String, read_only: Boolean) :
    RecyclerView.Adapter<MyTimeSlotRecyclerViewAdapter.ItemSlotViewHolder>() {
    var list = data.toMutableList()
    val userId: String = userId
    val read_only: Boolean = read_only

    class ItemSlotViewHolder(v: View, read_only: Boolean) : RecyclerView.ViewHolder(v) {
        private val title: TextView = v.findViewById(R.id.slot_title)
        private val date: TextView = v.findViewById(R.id.slot_date)
        private val time: TextView = v.findViewById(R.id.slot_time)
        private val duration: TextView = v.findViewById(R.id.slot_duration)
        val cv: CardView = v.findViewById(R.id.cv)
        val button: ImageButton = v.findViewById(R.id.button)

        fun bind(item: TimeSlotFire, read_only: Boolean) {
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            if(read_only){
                button.setImageResource(R.drawable.ic_person)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlotViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_time_slot, parent, false)
        return ItemSlotViewHolder(vg, read_only)
    }

    override fun onBindViewHolder(holder: ItemSlotViewHolder, position: Int) {
        val item = list[position]
        item.let { holder.bind(it, read_only) }
        val bundle = bundleOf("id" to item.id, "userId" to userId, "read_only" to read_only)

        holder.cv.setOnClickListener {
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
                bundle
            )
        }

        holder.button.setOnClickListener {
            if(read_only){
                findNavController(FragmentManager.findFragment(it)).navigate(
                    R.id.action_timeSlotListFragment_to_showProfileFragment,
                    bundle
                )
            }else {
                findNavController(FragmentManager.findFragment(it)).navigate(
                    R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                    bundle
                )
            }
        }
    }


    override fun getItemCount(): Int = list.size

}