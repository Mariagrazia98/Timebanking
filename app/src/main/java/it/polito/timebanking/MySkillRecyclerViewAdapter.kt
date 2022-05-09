package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MySkillRecyclerViewAdapter(val data: List<String>) :
    RecyclerView.Adapter<MySkillRecyclerViewAdapter.SkillViewHolder>(), Filterable {
    var list = data.toMutableList()
    var originalList = list

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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredRes: List<String> = getFilteredResults(constraint.toString().lowercase())
                val results = FilterResults()
                results.values = filteredRes
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                list = results?.values as MutableList<String>
                notifyDataSetChanged()
            }

        }
    }

    fun getFilteredResults(str: String): MutableList<String> {
        var text = str
        var results = mutableListOf<String>()
        if (text.isEmpty()) {
            results.addAll(originalList)
        } else {
            for (item in originalList) {
                if (item.toLowerCase().contains(text)) {
                    results.add(item)
                }
            }
        }
        return results
    }

    override fun getItemCount(): Int = list.size

}