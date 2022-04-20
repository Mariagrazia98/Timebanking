package it.polito.timebanking

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import it.polito.timebanking.placeholder.PlaceholderContent.PlaceholderItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */

data class ItemSlot(val id: Int, val title: String, val date: String, val time: String, val duration: Int)

class MyTimeSlotRecyclerViewAdapter(val data: MutableList<ItemSlot>) : RecyclerView.Adapter<MyTimeSlotRecyclerViewAdapter.ItemSlotViewHolder>() {

    var displayData = data.toMutableList()

    class ItemSlotViewHolder(v: View): RecyclerView.ViewHolder(v){
        private val title: TextView = v.findViewById(R.id.slot_title)
        private val date: TextView = v.findViewById(R.id.slot_date)
        private val time: TextView = v.findViewById(R.id.slot_time)
        private val duration: TextView = v.findViewById(R.id.slot_duration)
        val cv: CardView = v.findViewById(R.id.cv)
        val button: ImageButton = v.findViewById(R.id.button)

        fun bind(item: ItemSlot, action: (v: View)->Unit){
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            cv.setOnClickListener{
                findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
            }

            button.setOnClickListener{
                findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2)
            }

        }

        fun unbind(){}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlotViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_time_slot, parent, false)
        return ItemSlotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ItemSlotViewHolder, position: Int) {
        val item = displayData[position]

        holder.bind(item) {
            val pos = data.indexOf(item)
            if (pos!=-1) {
                data.removeAt(pos)
                val pos1 = displayData.indexOf(item)
                if (pos1!= -1) {
                    displayData.removeAt(pos1)
                    notifyItemRemoved(pos1)
                }
            }
        }
    }

    override fun getItemCount(): Int = displayData.size

}