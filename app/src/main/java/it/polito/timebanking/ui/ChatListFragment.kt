package it.polito.timebanking.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.MainActivity
import it.polito.timebanking.R
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class ChatListFragment : Fragment() {
    private lateinit var timeSlotVM: TimeSlotViewModel
    private lateinit var profileVM: ProfileViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String
    lateinit var slot : TimeSlot
    var adapter: ChatTitleAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        profileVM = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)

        val ev: TextView = view.findViewById(R.id.empty_view)
        recyclerView = view.findViewById(R.id.rv)

        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        slot = (arguments?.getSerializable("slot") as TimeSlot?)!!

        (activity as MainActivity).supportActionBar?.title = "Chats with interested users"

        timeSlotVM.getChatsSlotIncomingRequests(userId, slot.id).observe(viewLifecycleOwner) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            if(it!=null){
                adapter = ChatTitleAdapter(it, slot)
                recyclerView.adapter = adapter
            }

            if (it==null || it.isEmpty()) {
                recyclerView.visibility = View.GONE
                ev.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                ev.visibility = View.GONE
            }
        }

        return view
    }

}