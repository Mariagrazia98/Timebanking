package it.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.model.Chat
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*


class ChatFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var profileVM:ProfileViewModel
    lateinit var recyclerView: RecyclerView

    lateinit var userId: String
    lateinit var otherUser : User
    lateinit var slot : TimeSlot
    var chat:Chat?=null

    var mychats = false
    lateinit var chatTextView: EditText
    lateinit var sendButton: ImageButton
    lateinit var rejectButton: Button
    lateinit var assignButton:Button
    lateinit var reviewButton:Button
    lateinit var titleChat: TextView

    lateinit var askerId: String
    lateinit var offererId: String



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
        profileVM=ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        titleChat = view.findViewById(R.id.assignQuestion)
        chatTextView = view.findViewById(R.id.edit_gchat_message)
        sendButton = view.findViewById(R.id.button_gchat_send)
        rejectButton = view.findViewById(R.id.rejectButton)
        assignButton = view.findViewById(R.id.assignButton)
        reviewButton = view.findViewById(R.id.reviewButton)
        otherUser = (arguments?.getSerializable("user") as User?)!!
        slot = (arguments?.getSerializable("slot") as TimeSlot?)!!
        mychats = arguments?.getBoolean("mychats")?:false

        (activity as MainActivity).supportActionBar?.title = otherUser.fullname

        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        if(!mychats){
            offererId = otherUser.uid
            askerId = userId
        } else{
            offererId = userId
            askerId = otherUser.uid
        }

        recyclerView = view.findViewById(R.id.recycler_gchat)

        timeSlotVM.getChat(askerId, slot.id, offererId).observe(viewLifecycleOwner) {

            if (it != null) {
                chat = it
                if (slot.status== 0) {
                    reviewButton.visibility = View.GONE
                }
                if ( userId==offererId && slot.status== 0){
                    assignButton.visibility = View.VISIBLE
                    rejectButton.visibility = View.VISIBLE
                }
                if ( slot.status == 1){
                    assignButton.visibility = View.GONE
                    rejectButton.visibility = View.GONE
                    titleChat.visibility = View.GONE
                   // titleChat.setText("This timeslot request was rejected!")
                    if (slot.idReceiver == userId && (slot.reviewState == 2 || slot.reviewState == 3)) { //current user receiver
                        reviewButton.visibility = View.GONE
                    } else if (slot.idReceiver == userId && (slot.reviewState == 0 || slot.reviewState == 1)) {
                        reviewButton.visibility = View.VISIBLE
                    }
                    if (slot.idReceiver != userId && (slot.reviewState == 1 || slot.reviewState == 3)) { //current user offer
                        reviewButton.visibility = View.GONE
                    } else if (slot.idReceiver != userId && (slot.reviewState == 0 || slot.reviewState == 2)) {
                        reviewButton.visibility = View.VISIBLE
                    }
                }
                getChatMessages()
            }
            else{
                assignButton.visibility = View.GONE
                rejectButton.visibility = View.GONE
                reviewButton.visibility=View.GONE
                titleChat.visibility = View.GONE
            }
        }

        sendButton.setOnClickListener{
            if(chatTextView.text.toString() != "" && chat!= null){
                sendMessage()
            }else if(chatTextView.text.toString() != "" && chat == null){
                createChat()
            }
        }

        rejectButton.setOnClickListener{
            if(chat!=null){
                timeSlotVM.rejectChat(offererId, slot.id, chat!!.id)
                printMessage("This timeslot was refused with success for the this user")
                assignButton.visibility=View.GONE
                rejectButton.visibility=View.GONE
                titleChat.setText("This timeslot request was rejected")
            }
        }

        assignButton.setOnClickListener{
            profileVM.getUserById(chat!!.receiverUid).observe(viewLifecycleOwner) { userReceiver ->
                println(userReceiver?.fullname)
                println(userReceiver?.credit)
                println(slot.duration)
                if (userReceiver!!.credit <= slot.duration) {
                    printMessage("The user has not enough credit! It is not possible to assign the timeslot to him")
                } else {
                    profileVM.updateUserCredit( //decrement credit receiver
                        userReceiver.uid,
                        userReceiver.credit - slot.duration
                    ).observe(viewLifecycleOwner){
                        if(it){
                            profileVM.getUserById(chat!!.receiverUid).observe(viewLifecycleOwner
                            ) { userOffer ->
                                profileVM.updateUserCredit(
                                    offererId,//TODO UPDATE
                                    userOffer!!.credit + slot.duration
                                ) //increment credit offer
                                slot.status=1; //assigned
                                slot.idReceiver=chat!!.receiverUid
                                timeSlotVM.updateSlot(userId, slot)//update timeslot status
                            }
                        }
                    }
                }
            }
        }
        reviewButton.setOnClickListener{
            //TODO
        }
        return view
    }


    fun getChatMessages(){
        timeSlotVM.getSlotChatMessages(offererId, slot.id, chat!!.id).observe(viewLifecycleOwner){
            recyclerView.layoutManager = LinearLayoutManager(context)
            if(it!=null) {
                //controllare sorting
                var messages = it.sortedWith( compareBy({it.date}, {it.time})).toMutableList()
                val adapter = ChatAdapter(messages, userId, otherUser)
                recyclerView.adapter = adapter
            }
        }
    }

    fun sendMessage(){
        val msgId = timeSlotVM.getNewChatMessageId(offererId, slot.id, chat!!.id)
        val date = SimpleDateFormat("dd/MM/yyyy").format(Date()) //controllare
        val time = SimpleDateFormat("HH:mm").format(Date()) //controllare
        val msg = ChatMessage(msgId, userId, chatTextView.text.toString(), 0, date, time)
        val ret = timeSlotVM.addChatMessage(offererId, slot.id, chat!!.id, msg)
        chatTextView.setText("")
        getChatMessages()
    }

    fun createChat(){
        val chatId = timeSlotVM.getNewChatId(offererId, slot.id)
        timeSlotVM.addChat(offererId, slot.id, chatId.toString(), Chat(chatId.toString(), userId))
        sendMessage()
    }

    fun printMessage(message:String){
        val snackbar = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }
}