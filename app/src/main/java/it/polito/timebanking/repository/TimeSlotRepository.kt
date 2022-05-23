package it.polito.timebanking.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.model.Chat
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import kotlinx.coroutines.tasks.await

class TimeSlotRepository {

    fun getNewSlotId(userId: String): String {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("timeslots")
                .document()
                .id
            data
        } catch (e: Exception) {
            e.toString()
        }
    }

    suspend fun removeSlot(userId: String, slotId: String): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("timeslots")
                .document(slotId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateSlot(userId: String, slot: TimeSlot): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("timeslots")
                .document(slot.id)
                .set(slot)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getSlotsByUser(uid: String): Result<Map<User, List<TimeSlot>>> {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("timeslots")
                .get()
                .await()

            val user = Firebase.firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val result = mutableMapOf<User, List<TimeSlot>>()
            val slotsUser = mutableListOf<TimeSlot>()

            data.documents.map {
                if(it.data != null){
                    it.toObject(TimeSlot::class.java)?.let { it1 -> slotsUser.add(it1) }
                }
            }
            user.toObject(User::class.java).let { u ->
                if (u != null) {
                    result[u] = slotsUser
                }
            }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSlotFById(uid: String, slotId: String): Result<TimeSlot?> {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("timeslots")
                .document(slotId)
                .get()
                .await()

            Result.success(data.toObject(TimeSlot::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSkills(userId : String): Result<List<String>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .get()
                .await()

            val skills = mutableListOf<String>()
            users.forEach { user ->
                user.toObject(User::class.java).let { u ->
                    if (u.uid != userId) {
                        val timeslots = Firebase.firestore
                            .collection("users")
                            .document(u.uid)
                            .collection("timeslots")
                            .get()
                            .await()

                        timeslots.documents.map {
                            if (it.data != null) {
                                it.toObject(TimeSlot::class.java)?.let { it1 ->
                                    it1.skills.forEach { it2 ->
                                        skills.add(it2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return Result.success(skills)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getSlotsBySkill(userId: String, skill: String): Result<Map<User, List<TimeSlot>>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .get()
                .await()

            val filteredSlots = mutableMapOf<User, List<TimeSlot>>()
            val slotsUser = mutableListOf<TimeSlot>()
            users.forEach { user ->
                user.toObject(User::class.java).let { u ->
                    if (u.uid != userId) {
                        val timeslots = Firebase.firestore
                            .collection("users")
                            .document(u.uid)
                            .collection("timeslots")
                            .get()
                            .await()
                        timeslots.documents.map {
                            if (it.data != null) {
                                it.toObject(TimeSlot::class.java)?.let { it1 ->
                                    if (it1.skills.contains(skill)) {
                                        slotsUser.add(it1)
                                    }
                                }
                            }
                        }
                    }
                    if(slotsUser.isNotEmpty()){
                        filteredSlots[u] = ArrayList(slotsUser)
                        slotsUser.clear()
                    }
                }
            }
            return Result.success(filteredSlots)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }









    suspend fun getChatId(userId: String, slotId: String, uidOfferer: String): String? {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uidOfferer)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .whereEqualTo("receiverUid", userId)
                .get()
                .await()

            var chatId: String? = null
            data.documents.map{
                chatId = it.id
            }
            chatId
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun getNewChatId(userIdOfferer: String, slotId: String): String {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(userIdOfferer)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .document()
                .id
            data
        } catch (e: Exception) {
            e.toString()
        }
    }

    suspend fun addChat(userIdOfferer: String, slotId: String, chatId: String, chat: Chat): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(userIdOfferer)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .document(chatId)
                .set(chat, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getNewChatMessageId(userId: String, slotId: String, chatId: String): String {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .document(chatId)
                .collection("messageList")
                .document()
                .id
            data
        } catch (e: Exception) {
            e.toString()
        }
    }

    suspend fun addChatMessage(userIdOfferer: String, slotId: String, chatId: String, msg: ChatMessage): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(userIdOfferer)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .document(chatId)
                .collection("messageList")
                .document(msg.id)
                .set(msg, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    //retrieve the chat messages exchanged between the offerer of a specific timeslot and the current user who is asking the timeslot
    suspend fun getSlotChatWithOfferer(uidCurrent: String, uidOfferer: String, slotId: String): Result<List<ChatMessage>?> {
        return try {
            val chats = Firebase.firestore
                .collection("users")
                .document(uidOfferer)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .whereEqualTo("receiverUid", uidCurrent)
                .get()
                .await()

            var messageList: MutableList<ChatMessage>? = mutableListOf()
            chats.documents.map{
                val chatId = it.id
                val list = Firebase.firestore
                    .collection("users")
                    .document(uidOfferer)
                    .collection("timeslots")
                    .document(slotId)
                    .collection("chats")
                    .document(chatId)
                    .collection("messageList")
                    .get()
                    .await()

                list.forEach{
                    messageList?.add(it.toObject(ChatMessage::class.java))
                }
            }
            Result.success(messageList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //retrieve all started chats (incoming requests by other user to the offerer -> current user) for a specific timeslot
    suspend fun getChatsSlotIncomingRequests(uidCurrent: String, slotId: String) : Result<List<Chat>?> {
        return try {
            val chats = Firebase.firestore
                .collection("users")
                .document(uidCurrent)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .get()
                .await()

            val allChats : MutableList<Chat>? = chats.toObjects(Chat::class.java)

            Result.success(allChats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //retrieve the chat messages exchanged between the current user (offerer) and another user who is asking for the timeslot
    suspend fun getSlotChatWithAsker(uidCurrent: String, slotId: String, chat: Chat): Result<List<ChatMessage>?> {
        return try {
            val messages = Firebase.firestore
                .collection("users")
                .document(uidCurrent)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .document(chat.id)
                .collection("messageList")
                .get()
                .await()

            val messageList : MutableList<ChatMessage>? = messages.toObjects(ChatMessage::class.java)

            Result.success(messageList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}