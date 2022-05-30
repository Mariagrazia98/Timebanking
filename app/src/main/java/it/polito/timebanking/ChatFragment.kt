package it.polito.timebanking

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.model.Chat
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.ReviewViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {
    private lateinit var timeSlotVM: TimeSlotViewModel
    private lateinit var profileVM: ProfileViewModel
    lateinit var recyclerView: RecyclerView

    lateinit var userId: String
    private lateinit var otherUser: User
    lateinit var slot: TimeSlot
    private var chat: Chat? = null

    private var mychats = false
    private lateinit var chatTextView: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var rejectButton: Button
    private lateinit var assignButton: Button
    private lateinit var reviewButton: Button
    private lateinit var titleChat: TextView

    private lateinit var askerId: String
    private lateinit var offererId: String
    private lateinit var reviewsVM: ReviewViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        reviewsVM = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)
        profileVM = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        titleChat = view.findViewById(R.id.assignQuestion)
        chatTextView = view.findViewById(R.id.edit_gchat_message)
        sendButton = view.findViewById(R.id.button_gchat_send)
        rejectButton = view.findViewById(R.id.rejectButton)
        assignButton = view.findViewById(R.id.assignButton)
        reviewButton = view.findViewById(R.id.reviewButton)
        otherUser = (arguments?.getSerializable("user") as User?)!!
        val slotId = (arguments?.getSerializable("slot") as TimeSlot?)!!.id

        mychats = arguments?.getBoolean("mychats") ?: false

        (activity as MainActivity).supportActionBar?.title = otherUser.fullname

        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        if (!mychats) {
            offererId = otherUser.uid
            askerId = userId
        } else {
            offererId = userId
            askerId = otherUser.uid
        }

        recyclerView = view.findViewById(R.id.recycler_gchat)

        timeSlotVM.getSlotFById(offererId, slotId).observe(viewLifecycleOwner) {
            slot = it
            getChat(false)
        }

        sendButton.setOnClickListener {
            if (chatTextView.text.toString() != "" && chat != null) {
                sendMessage()
            } else if (chatTextView.text.toString() != "" && chat == null) {
                createChat()
            }
        }

        rejectButton.setOnClickListener {
            if (chat != null) {
                timeSlotVM.rejectChat(offererId, slot.id, chat!!.id)
                printMessage("This timeslot was refused with success for the this user")
                assignButton.visibility = View.GONE
                rejectButton.visibility = View.GONE
                titleChat.text = "This timeslot request was rejected"
            }
        }

        assignButton.setOnClickListener {
            profileVM.getUserById(chat!!.receiverUid).observe(viewLifecycleOwner) { userReceiver ->
                if (userReceiver!!.credit <= slot.duration) {
                    printMessage("The user has not enough credit! It is not possible to assign the timeslot to him")
                } else {
                    profileVM.updateUserCredit( //decrement credit receiver
                        userReceiver.uid,
                        userReceiver.credit - slot.duration
                    ).observe(viewLifecycleOwner) {
                        if (it) {
                            profileVM.getUserById(chat!!.receiverUid).observe(
                                viewLifecycleOwner
                            ) { userOffer ->
                                profileVM.updateUserCredit(
                                    offererId,
                                    userOffer!!.credit + slot.duration
                                ) //increment credit offer
                                slot.status = 1 //assigned
                                slot.idReceiver = chat!!.receiverUid
                                timeSlotVM.updateSlot(userId, slot)//update timeslot status
                                titleChat.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }

        reviewButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userIdReviewer", userId) //logged user is doing review
            bundle.putString("userId", otherUser.uid)
            bundle.putString("timeslotId", slotId)
            findNavController().navigate(R.id.action_chatFragment_to_editReviewFragment, bundle)
        }

        reviewsVM.getReviewsByUser(otherUser.uid).observe(viewLifecycleOwner) {
            //check if I have already reviewed him
            //if yes, disable button
            for (ith in it) {
                if (ith.timeSlotId == slotId && ith.userIdReviewer == userId) {
                    reviewButton.isEnabled = false
                    Toast.makeText(context, "You already reviewed this!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }


    private fun getChat(send: Boolean) {
        timeSlotVM.getChat(askerId, slot.id, offererId).observe(viewLifecycleOwner) {

            if (it != null) {
                chat = it
                if (chat!!.chatStatus == 1) {
                    reviewButton.visibility = View.GONE
                    assignButton.visibility = View.GONE
                    rejectButton.visibility = View.GONE
                    titleChat.visibility = View.VISIBLE
                    titleChat.text = "This timeslot request was rejected!"
                } else {
                    if (slot.status == 0) {
                        reviewButton.visibility = View.GONE
                        titleChat.visibility = View.GONE
                    }
                    if (userId == offererId && slot.status == 0) {
                        assignButton.visibility = View.VISIBLE
                        rejectButton.visibility = View.VISIBLE
                        titleChat.visibility = View.VISIBLE
                    }
                    if (slot.status == 1) { //timeslot assigned
                        assignButton.visibility = View.GONE
                        rejectButton.visibility = View.GONE
                        reviewButton.visibility = View.GONE
                        if ((slot.idReceiver == userId && (slot.reviewState == 0 || slot.reviewState == 1)) ||
                            (slot.idReceiver != userId && (slot.reviewState == 0 || slot.reviewState == 2))
                        ) {
                            reviewButton.visibility = View.VISIBLE
                        }
                    }
                }
                if (send) {
                    sendMessage()
                }
                getChatMessages()
            } else {
                assignButton.visibility = View.GONE
                rejectButton.visibility = View.GONE
                reviewButton.visibility = View.GONE
                titleChat.visibility = View.GONE
            }
        }
    }


    private fun getChatMessages() {
        timeSlotVM.getSlotChatMessagesB(offererId, slot.id, chat!!.id).observe(viewLifecycleOwner) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            if (it != null) {
                //controllare sorting
                val messages =
                    it.distinctBy { it.id }.sortedWith(compareBy({ it.date }, { it.time }))
                        .toMutableList()
                val adapter = ChatAdapter(messages, userId, otherUser)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun sendMessage() {
        val msgId = timeSlotVM.getNewChatMessageId(offererId, slot.id, chat!!.id)
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(Date()) //TODO: controllare
        val time = SimpleDateFormat("HH:mm:ss", Locale.ITALY).format(Date()) //TODO: controllare
        val msg = ChatMessage(msgId, userId, chatTextView.text.toString(), 0, date, time)
        timeSlotVM.addChatMessage(offererId, slot.id, chat!!.id, msg)
        chatTextView.setText("")
    }

    private fun createChat() {
        val chatId = timeSlotVM.getNewChatId(offererId, slot.id)
        timeSlotVM.addChat(offererId, slot.id, chatId, Chat(chatId, userId))
        getChat(true) //get chat and send message
    }

    private fun printMessage(message: String) {
        val snackbar = Snackbar.make(
            requireView(),
            message,
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }
}