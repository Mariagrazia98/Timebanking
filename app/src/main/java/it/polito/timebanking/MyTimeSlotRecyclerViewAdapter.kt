package it.polito.timebanking

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.model.UserFire


class MyTimeSlotRecyclerViewAdapter(
    val data: Map<UserFire, List<TimeSlotFire>>,
    read_only: Boolean
) :


    RecyclerView.Adapter<MyTimeSlotRecyclerViewAdapter.ItemSlotViewHolder>(), Filterable {
    var list = data.toMutableMap().values.flatten()
    var originalList = data.toMutableMap().values.flatten()
    val read_only: Boolean = read_only

    class ItemSlotViewHolder(v: View, read_only: Boolean) : RecyclerView.ViewHolder(v) {
        private val title: TextView = v.findViewById(R.id.slot_title)
        private val date: TextView = v.findViewById(R.id.slot_date)
        private val time: TextView = v.findViewById(R.id.slot_time)
        private val duration: TextView = v.findViewById(R.id.slot_duration)
        private val name: TextView? = v.findViewById(R.id.offererName)
        val cv: CardView = v.findViewById(R.id.cv)
        val button: ImageButton? = v.findViewById(R.id.button)
        val ivSlot: ImageView? = v.findViewById(R.id.imageViewSlot)


        fun bind(item: TimeSlotFire, read_only: Boolean, user: UserFire) {
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            if (read_only) {
                if (ivSlot != null) {
                    //Glide.with(requireContext()).load(user.imagePath).into(ivSlot) TODO
                }
                name?.text = user.fullname

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSlotViewHolder {
        var vg: View?
        if (read_only)
            vg = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_other_time_slot, parent, false)
        else
            vg = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_my_time_slot, parent, false)
        return ItemSlotViewHolder(vg, read_only)
    }

    override fun onBindViewHolder(holder: ItemSlotViewHolder, position: Int) {
        val item = list[position]
        lateinit var user: UserFire
        data.toMutableMap().keys.forEach {
            if (data.toMutableMap()[it]?.contains(item) == true) {
                user = it
            }
        }
        item.let { holder.bind(it, read_only, user) }
        val bundle = bundleOf("read_only" to read_only)
        bundle.putSerializable("user", user)
        bundle.putSerializable("slot", item)

        holder.cv.setOnClickListener {
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
                bundle
            )
        }

        holder.button?.setOnClickListener {
            if (read_only) {
                findNavController(FragmentManager.findFragment(it)).navigate(
                    R.id.action_timeSlotListFragment_to_showProfileFragment,
                    bundle
                )
            } else {
                findNavController(FragmentManager.findFragment(it)).navigate(
                    R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                    bundle
                )
            }
        }
    }


    override fun getItemCount(): Int = list.size

    override fun getFilter(): Filter {
        return object : Filter() {
            var resCount = 0

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredRes: List<TimeSlotFire>? = null
                if (constraint.startsWith("date"))
                    filteredRes = getFilteredResultsByDate(constraint.toString().lowercase())
                else if (constraint.startsWith("duration"))
                    filteredRes = getFilteredResultsByDuration(constraint.toString().lowercase())
                else if (constraint.startsWith("time"))
                    filteredRes = getFilteredResultsByStartTime(constraint.toString().lowercase())
                else if (constraint.startsWith("order=date"))
                    filteredRes = list.sortedWith { x, y -> x.date.compareTo(y.date) }
                else if (constraint.startsWith("order=duration"))
                    filteredRes = list.sortedWith { x, y -> x.duration.compareTo(y.duration) }
                else
                    filteredRes = originalList

                resCount = filteredRes.size
                val results = FilterResults()
                results.values = filteredRes
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (resCount == 0)
                    list = arrayListOf()
                else
                    list = results?.values as MutableList<TimeSlotFire>
                notifyDataSetChanged()
            }
        }
    }

    fun getFilteredResultsByDate(str: String): MutableList<TimeSlotFire> {
        var results = mutableListOf<TimeSlotFire>()
        var date = str.split('=')[1]

        for (item in originalList) {
            if (item.date == date) {
                results.add(item)
            }
        }

        return results
    }

    fun getFilteredResultsByDuration(str: String): MutableList<TimeSlotFire> {
        var results = mutableListOf<TimeSlotFire>()
        var duration = str.split('=')[1]
        for (item in originalList) {
            if (item.duration >= duration.toInt()) {
                results.add(item)
            }
        }
        return results
    }

    fun getFilteredResultsByStartTime(str: String): MutableList<TimeSlotFire> {
        var results = mutableListOf<TimeSlotFire>()
        var duration = str.split('=')[1]

        for (item in originalList) {
            if (item.time >= duration) {
                results.add(item)
            }
        }

        return results
    }

}