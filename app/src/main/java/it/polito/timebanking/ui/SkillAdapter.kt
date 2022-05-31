package it.polito.timebanking.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.R

class SkillAdapter(val data: List<String>, id: String) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>(), Filterable {

    var list = data.toMutableList()
    var originalList = data.toMutableList()
    val userId: String = id

    class SkillViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cv: CardView = v.findViewById(R.id.cvLastRating)
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
        val read_only = true
        val bundle = bundleOf("userId" to userId, "read_only" to read_only, "skill" to item)
        holder.cv.setOnClickListener {
            NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_skillsListFragment_to_timeSlotListFragment,
                bundle
            )
        }

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
        val text = str
        val results = mutableListOf<String>()
        if (text.isEmpty()) {
            results.addAll(originalList)
        } else {
            for (item in originalList) {
                if (item.lowercase().contains(text)) {
                    results.add(item)
                }
            }
        }
        return results
    }

    override fun getItemCount(): Int = list.size

}