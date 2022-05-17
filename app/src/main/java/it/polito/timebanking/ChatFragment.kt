package it.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.model.UserFire


class Message(var message: String, var sender: UserFire, var createdAt: Long) {}
class User(var nickname: String, var profileUrl: String) {}

class ChatFragment : Fragment() {
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String

    var messageList: List<Message> = listOf(Message("Ciao", UserFire("Pietro", "url"), 22), Message("Come stai", UserFire("Fabio", "url"), 23))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        recyclerView = view.findViewById(R.id.recycler_gchat)
        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MessageListRecyclerViewAdapter(messageList)
        recyclerView.adapter = adapter

        return view
    }


}