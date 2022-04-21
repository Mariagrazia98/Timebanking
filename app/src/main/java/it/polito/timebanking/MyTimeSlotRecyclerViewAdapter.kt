package it.polito.timebanking

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
import it.polito.timebanking.repository.Slot

class MyTimeSlotRecyclerViewAdapter(val data: List<Slot>): RecyclerView.Adapter<MyTimeSlotRecyclerViewAdapter.ItemSlotViewHolder>() {
    var list = data.toMutableList()

    class ItemSlotViewHolder(v: View): RecyclerView.ViewHolder(v){
        private val title: TextView = v.findViewById(R.id.slot_title)
        private val date: TextView = v.findViewById(R.id.slot_date)
        private val time: TextView = v.findViewById(R.id.slot_time)
        private val duration: TextView = v.findViewById(R.id.slot_duration)
        val cv: CardView = v.findViewById(R.id.cv)
        val button: ImageButton = v.findViewById(R.id.button)

        fun bind(item: Slot, action: (v: View)->Unit){
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            var bundle = bundleOf("id" to item.id)

            cv.setOnClickListener{
                findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, bundle)
            }

            button.setOnClickListener{
                findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2, bundle)
            }

        }

        fun unbind(){}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlotViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_time_slot, parent, false)
        return ItemSlotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemSlotViewHolder, position: Int) {
        val item = list[position]

        holder.bind(item) {
            val pos = list.indexOf(item)
            if (pos!=-1) {
                list.removeAt(pos)
                val pos1 = list.indexOf(item)
                if (pos1!= -1) {
                    list.removeAt(pos1)
                    notifyItemRemoved(pos1)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

}