package it.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.model.Chat
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var recyclerView: RecyclerView

    lateinit var userId: String
    lateinit var userOfferer : User
    lateinit var slot : TimeSlot
    var read_only : Boolean = false
    var chatId: String? = null

    var mychats = false
    lateinit var chatTextView: EditText
    lateinit var sendButton: ImageButton



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
        val assignTimeSlot = view.findViewById<LinearLayout>(R.id.linearLayout4)

        userOfferer = (arguments?.getSerializable("user") as User?)!!
        slot = (arguments?.getSerializable("slot") as TimeSlot?)!!
        mychats = arguments?.getBoolean("mychats")?:false
        read_only = arguments?.getBoolean("read_only")?:false
        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        recyclerView = view.findViewById(R.id.recycler_gchat)

        if(!mychats){
            timeSlotVM.getChatId(userId, slot.id, userOfferer.uid).observe(viewLifecycleOwner){
                if(it!=null){
                    chatId = it
                    getChatMessages()
                }
            }
        }else if(mychats){
            val chatsList = timeSlotVM.getChatsSlotIncomingRequests(userId, slot.id)
            //val chat = chatsList.value?.get(0)
            chatsList.observe(viewLifecycleOwner){
                if(it!=null){
                    val chat = it[0]
                    //prova -> temporaneo //"ZDqza5uYGOCF49OK2EkQ"
                    timeSlotVM.getSlotChatWithAsker(userId, slot.id, chat?.id!!).observe(viewLifecycleOwner){
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        if(it!=null) {
                            //controllare sorting
                            var messages = it.sortedWith( compareBy({it.date}, {it.time})).toMutableList()
                            val adapter = ChatAdapter(messages, userId, userOfferer)
                            recyclerView.adapter = adapter
                        }
                    }
                }
            }
        }

        chatTextView = view.findViewById(R.id.edit_gchat_message)
        sendButton = view.findViewById(R.id.button_gchat_send)

        (activity as MainActivity).supportActionBar?.title = userOfferer.fullname

        sendButton.setOnClickListener{
            if(chatTextView.text.toString() != "" && chatId != null){
                sendMessage()
            }else if(chatTextView.text.toString() != "" && chatId == null){
                createChat()
            }
        }

        if(read_only){
            assignTimeSlot.visibility = View.GONE
        }else{
            assignTimeSlot.visibility = View.VISIBLE
        }

        return view
    }




    fun getChatMessages(){
        timeSlotVM.getSlotChatWithOfferer(userId, userOfferer.uid, slot.id, chatId!!).observe(viewLifecycleOwner){
            recyclerView.layoutManager = LinearLayoutManager(context)
            if(it!=null) {
                //controllare sorting
                var messages = it.sortedWith( compareBy({it.date}, {it.time})).toMutableList()
                val adapter = ChatAdapter(messages, userId, userOfferer)
                recyclerView.adapter = adapter
            }
        }
    }

    fun sendMessage(){
        val msgId = timeSlotVM.getNewChatMessageId(userId, slot.id, chatId!!)
        val date = SimpleDateFormat("dd/MM/yyyy").format(Date()) //controllare
        val time = SimpleDateFormat("HH:mm").format(Date()) //controllare
        val msg = ChatMessage(msgId, userId, chatTextView.text.toString(), 0, date, time)
        val ret = timeSlotVM.addChatMessage(userOfferer.uid, slot.id, chatId!!, msg)
        chatTextView.setText("")
        getChatMessages()
    }

    fun createChat(){
        chatId = timeSlotVM.getNewChatId(userOfferer.uid, slot.id)
        val chat = Chat(chatId.toString(), userId)
        timeSlotVM.addChat(userOfferer.uid, slot.id, chatId.toString(), chat)
        sendMessage()
    }

}