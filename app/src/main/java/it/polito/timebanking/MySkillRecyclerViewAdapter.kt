package it.polito.timebanking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.repository.Slot

class MySkillRecyclerViewAdapter(val data: List<Slot>) :
    RecyclerView.Adapter<MySkillRecyclerViewAdapter.SkillViewHolder>() {
        var list = data.toMutableList()

        class SkillViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val cv: CardView = v.findViewById(R.id.cv)

            fun bind(item: Slot) {

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
            val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_skill, parent, false)
            return SkillViewHolder(vg)
        }

        override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
            val item = list[position]
            item.let { holder.bind(it) }
            val bundle = bundleOf("id" to item.id)

            holder.cv.setOnClickListener {
                NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(
                    R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
                    bundle
                )
            }
        }


        override fun getItemCount(): Int = list.size

    }