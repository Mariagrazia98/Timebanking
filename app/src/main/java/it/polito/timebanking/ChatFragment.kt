package it.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.model.User

class MessageItem(val content:String){}
class MessageItemUi(val content:String, val textColor:Int, val messageType:Int){
    companion object {
        const val TYPE_MY_MESSAGE = 0
        const val TYPE_FRIEND_MESSAGE = 1
    }
}

class ChatFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String
    lateinit var user : User

    lateinit var profileNameView : TextView
    lateinit var profileImageView : ImageView


    var messageList: MutableList<MessageItemUi> = mutableListOf(
        MessageItemUi("Ciao", 222, 0),
        MessageItemUi("Come stai?", 222, 1),
        MessageItemUi("Tutto bene te?", 222, 0),
        MessageItemUi("Bene grazie", 222, 1),
        MessageItemUi("Prova prova", 222, 0),
        MessageItemUi("Bello bello", 222, 1),
    )


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        user = (arguments?.getSerializable("user") as User?)!!

        recyclerView = view.findViewById(R.id.recycler_gchat)
        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ChatAdapter(messageList, user)
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        profileNameView = view.findViewById(R.id.offererName)
        profileImageView = view.findViewById(R.id.imageViewSlot)

        profileNameView.text = user.fullname
        // Download directly from StorageReference using Glide
        if(user.imagePath!=null)
            Glide.with(this /* context */).load(user.imagePath).into(profileImageView)

    }


}