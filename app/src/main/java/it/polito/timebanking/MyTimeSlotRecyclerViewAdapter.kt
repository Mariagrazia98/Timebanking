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


class MyTimeSlotRecyclerViewAdapter(
    val data: Map<User, List<TimeSlot>>,
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


        fun bind(item: TimeSlot, read_only: Boolean, user: User) {
            title.text = item.title
            date.text = item.date
            time.text = item.time
            duration.text = item.duration.toString()

            if (read_only) {
                if (ivSlot != null) {
                    Glide.with(ivSlot.context).load(user.imagePath).into(ivSlot)
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
        lateinit var user: User
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
            findNavController(FragmentManager.findFragment(it)).navigate(
                R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                bundle
            )
        }
    }


    override fun getItemCount(): Int = list.size

    override fun getFilter(): Filter {
        return object : Filter() {
            var resCount = 0

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredRes: List<TimeSlot>? = null
                if (constraint.startsWith("date"))
                    filteredRes = getFilteredResultsByDate(constraint.toString().lowercase())
                else if (constraint.startsWith("duration"))
                    filteredRes = getFilteredResultsByDuration(constraint.toString().lowercase())
                else if (constraint.startsWith("time"))
                    filteredRes = getFilteredResultsByStartTime(constraint.toString().lowercase())
                else if (constraint.startsWith("order=Date (ascending)")) {
                    filteredRes = list.sortedWith { x, y -> x.date.compareTo(y.date) }
                } else if (constraint.startsWith("order=Date (descending)")) {
                    filteredRes = list.sortedWith { x, y -> -x.date.compareTo(y.date) }
                } else if (constraint.startsWith("order=Duration (ascending)")) {
                    filteredRes = list.sortedWith { x, y -> x.duration.compareTo(y.duration) }
                } else if (constraint.startsWith("order=Duration (descending)")) {
                    filteredRes = list.sortedWith{x, y -> -(x.duration.compareTo(y.duration))}
                } else
                    filteredRes = originalList

                resCount = filteredRes!!.size
                val results = FilterResults()
                results.values = filteredRes
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (resCount == 0)
                    list = arrayListOf()
                else
                    list = results?.values as MutableList<TimeSlot>
                notifyDataSetChanged()
            }
        }
    }

    fun getFilteredResultsByDate(str: String): MutableList<TimeSlot> {
        var results = mutableListOf<TimeSlot>()
        var date = str.split('=')[1]

        for (item in list) {
            if (item.date == date) {
                results.add(item)
            }
        }

        return results
    }

    fun getFilteredResultsByDuration(str: String): MutableList<TimeSlot> {
        var results = mutableListOf<TimeSlot>()
        var duration = str.split('=')[1].replace("\\s".toRegex(), "").split("-")
        var sx = duration[0].toInt()
        var dx = duration[1].toInt()
        for (item in list) {
            if (item.duration >= sx && item.duration <= dx) {
                results.add(item)
            }
        }
        return results
    }

    fun getFilteredResultsByStartTime(str: String): MutableList<TimeSlot> {
        var results = mutableListOf<TimeSlot>()
        var duration = str.split('=')[1]

        for (item in list) {
            if (item.time >= duration) {
                results.add(item)
            }
        }

        return results
    }

}