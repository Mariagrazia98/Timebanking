package it.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

/*
class MessageItem(val content:String){}
class ChatMessage(val content:String, val textColor:Int, val messageType:Int){
    companion object {
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
    }
}
*/

class ChatFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String
    lateinit var userOfferer : User
    lateinit var slot : TimeSlot
    var chatId: String? = null
    
    lateinit var chatTextView: EditText
    lateinit var sendButton: ImageButton

/*
    var messageList: MutableList<ChatMessage> = mutableListOf(
        ChatMessage("Ciao", 222, 0),
        ChatMessage("Come stai?", 222, 1),
        ChatMessage("Tutto bene te?", 222, 0),
        ChatMessage("Bene grazie", 222, 1),
        ChatMessage("Prova prova", 222, 0),
        ChatMessage("Bello bello", 222, 1),
    )
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        userOfferer = (arguments?.getSerializable("user") as User?)!!
        slot = (arguments?.getSerializable("slot") as TimeSlot?)!!

        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        recyclerView = view.findViewById(R.id.recycler_gchat)

        timeSlotVM.getChatId(userId, slot.id, userOfferer.uid).observe(viewLifecycleOwner){
            if(it!=null)
                chatId = it
        }

        timeSlotVM.getSlotChatWithOfferer(userId, userOfferer.uid, slot.id).observe(viewLifecycleOwner){
            recyclerView.layoutManager = LinearLayoutManager(context)
            if(it!=null) {
                //controllare sorting
                val messages = it.sortedWith( compareBy({it.date}, {it.time}))
                val adapter = ChatAdapter(messages, userOfferer)
                recyclerView.adapter = adapter
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatTextView = view.findViewById(R.id.edit_gchat_message)
        sendButton = view.findViewById(R.id.button_gchat_send)

        (activity as MainActivity).supportActionBar?.title = userOfferer.fullname

        sendButton.setOnClickListener{
            if(chatTextView.text.toString() != "" && chatId != null){
                val msgId = timeSlotVM.getNewChatMessageId(userId, slot.id, chatId!!)
                val date = SimpleDateFormat("dd/MM/yyyy").format(Date()) //controllare
                val time = SimpleDateFormat("hh:mm").format(Date()) //controllare
                var msg = ChatMessage(msgId, chatTextView.text.toString(), 0, date, time)
                val ret = timeSlotVM.addChatMessage(userOfferer.uid, slot.id, chatId!!, msg)
                chatTextView.setText("")
            }
        }

    }


}