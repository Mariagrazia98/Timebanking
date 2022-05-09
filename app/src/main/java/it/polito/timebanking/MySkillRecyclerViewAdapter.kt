package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MySkillRecyclerViewAdapter(val data: List<String>) :
    RecyclerView.Adapter<MySkillRecyclerViewAdapter.SkillViewHolder>() {
    var list = data.toMutableList()
    class SkillViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cv: CardView = v.findViewById(R.id.cv)
        private val title: TextView = v.findViewById(R.id.skill_title)

        fun bind(item: String) {
            title.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_skill, parent, false)
        return SkillViewHolder(vg)
    }


    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val item = list[position]
        item.let { holder.bind(it) }
        /*
        val bundle = bundleOf("id" to item.id)

        holder.cv.setOnClickListener {
            NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
                bundle
            )
        }
        */
    }

    fun filter(text: String) {
        var text = text
        items.clear()
        if (text.isEmpty()) {
            items.addAll(itemsCopy)
        } else {
            text = text.toLowerCase()
            for (item in itemsCopy) {
                if (item.name.toLowerCase().contains(text) || item.phone.toLowerCase().contains(text)
                ) {
                    items.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

}