package it.polito.timebanking

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        //private val editButton: Button = v.findViewById(R.id.edit_slot_button)

        fun bind(item: ItemSlot, action: (v: View)->Unit){
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()
        }

        fun unbind(){}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlotViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_time_slot_list, parent, false)
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