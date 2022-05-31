package it.polito.timebanking.repository

import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.model.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    fun getSlotFById(uid: String, slotId: String): DocumentReference {
        val docRef = Firebase.firestore
            .collection("users")
            .document(uid)
            .collection("timeslots")
            .document(slotId)

        return docRef
    }

    suspend fun getAllSkills(userId : String): Result<List<String>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .whereNotEqualTo("uid", userId)
                .get()
                .await()

            val skills = mutableListOf<String>()
            users.forEach { user ->
                user.toObject(User::class.java).let { u ->
                    val timeslots = Firebase.firestore
                        .collection("users")
                        .document(u.uid)
                        .collection("timeslots")
                        .whereEqualTo("status", 0)
                        .get()
                        .await()

                    timeslots.documents.map {
                        if (it.data != null) {
                            it.toObject(TimeSlot::class.java)?.let { it1 ->
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)

                                if(formatter.parse(it1.date)!! >= Date()){
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
                .whereNotEqualTo("uid", userId)
                .get()
                .await()

            val filteredSlots = mutableMapOf<User, List<TimeSlot>>()
            val slotsUser = mutableListOf<TimeSlot>()
            users.forEach { user ->
                user.toObject(User::class.java).let { u ->
                    val timeslots = Firebase.firestore
                        .collection("users")
                        .document(u.uid)
                        .collection("timeslots")
                        .whereEqualTo("status", 0)
                        .get()
                        .await()
                    timeslots.documents.map {
                        if (it.data != null) {
                            it.toObject(TimeSlot::class.java)?.let { it1 ->
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)

                                if(formatter.parse(it1.date)!! >= Date()) {
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

    suspend fun getInterestedSlotsByUser(userId: String): Result<Map<User, List<TimeSlot>>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .whereNotEqualTo("uid", userId)
                .get()
                .await()

            val filteredSlots = mutableMapOf<User, List<TimeSlot>>()
            val slotsUser = mutableListOf<TimeSlot>()
            users.forEach { user ->
                user.toObject(User::class.java).let { u ->
                    val timeslots = Firebase.firestore
                        .collection("users")
                        .document(u.uid)
                        .collection("timeslots")
                        .get()
                        .await()

                    timeslots.forEach { timeslot ->
                        timeslot.toObject(TimeSlot::class.java).let { t ->
                            val chats = Firebase.firestore
                                .collection("users")
                                .document(u.uid)
                                .collection("timeslots")
                                .document(t.id)
                                .collection("chats")
                                .whereEqualTo("receiverUid", userId)
                                .get()
                                .await()

                            chats.documents.map {
                                if (it.data != null) {
                                    it.toObject(Chat::class.java).let { it1 ->
                                        slotsUser.add(t)
                                    }
                                }
                            }
                        }
                    }
                    if (slotsUser.isNotEmpty()) {
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


    suspend fun getAcceptedSlotsByUser(userId: String): Result<Map<User, List<TimeSlot>>> {
        try {
            val filteredSlots = mutableMapOf<User, List<TimeSlot>>()
            var receiver: User? = null

            val timeslots = Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("timeslots")
                .whereEqualTo("status", 1)
                .get()
                .await()

            timeslots.documents.map {
                if (it.data != null) {
                    it.toObject(TimeSlot::class.java)?.let { timeslot ->
                        receiver = timeslot.idReceiver?.let { receiver ->
                            Firebase.firestore
                                .collection("users")
                                .document(receiver)
                                .get()
                                .await()
                                .toObject(User::class.java)
                        }

                        receiver?.let{ r ->
                            if(!filteredSlots.containsKey(r))
                                filteredSlots[r] = listOf()
                            val tmp = filteredSlots[r]?.toMutableList()
                            tmp?.add(timeslot)
                            filteredSlots[r] = tmp!!
                        }
                    }
                }
            }
            return Result.success(filteredSlots)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getAssignedSlotsByUser(userId: String): Result<Map<User, List<TimeSlot>>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .whereNotEqualTo("uid", userId)
                .get()
                .await()

            val filteredSlots = mutableMapOf<User, List<TimeSlot>>()
            val slotsUser = mutableListOf<TimeSlot>()
            users.forEach { user ->
                user.toObject(User::class.java).let { u ->
                    val timeslots = Firebase.firestore
                        .collection("users")
                        .document(u.uid)
                        .collection("timeslots")
                        .whereEqualTo("status", 1)
                        .whereEqualTo("idReceiver", userId)
                        .get()
                        .await()

                    timeslots.forEach { timeslot ->
                        timeslot.toObject(TimeSlot::class.java).let { t ->
                            slotsUser.add(t)
                        }
                    }
                    if (slotsUser.isNotEmpty()) {
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

    /*** CHAT ***/
    fun getChat(idAsker: String, slotId: String, idOfferer: String): Query {
        val docRef = Firebase.firestore
            .collection("users")
            .document(idOfferer)
            .collection("timeslots")
            .document(slotId)
            .collection("chats")
            .whereEqualTo("receiverUid", idAsker)

        return docRef
    }

    fun getNewChatId(idOfferer: String, slotId: String): String {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(idOfferer)
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

    suspend fun addChat(idOfferer: String, slotId: String, chatId: String, chat: Chat): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(idOfferer)
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

    fun getNewChatMessageId(idOfferer: String, slotId: String, chatId: String): String {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(idOfferer)
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

    suspend fun addChatMessage(idOfferer: String, slotId: String, chatId: String, msg: ChatMessage): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(idOfferer)
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
    fun getSlotChatMessages(idOfferer: String, slotId: String, chatId: String): CollectionReference {
        val colRef = Firebase.firestore
            .collection("users")
            .document(idOfferer)
            .collection("timeslots")
            .document(slotId)
            .collection("chats")
            .document(chatId)
            .collection("messageList")

        return colRef
    }

    //retrieve all started chats (incoming requests by other user to the offerer -> current user) for a specific timeslot
    suspend fun getChatsSlotIncomingRequests(idOfferer: String, slotId: String) : Result<List<ChatUser>?> {
        return try {
            val chats = Firebase.firestore
                .collection("users")
                .document(idOfferer)
                .collection("timeslots")
                .document(slotId)
                .collection("chats")
                .get()
                .await()

            val chatList: MutableList<ChatUser> = mutableListOf()
            chats.forEach{
                val chatMap = it.data

                val otherUserId = chatMap.getValue("receiverUid").toString()
                val user = Firebase.firestore
                    .collection("users")
                    .document(otherUserId)
                    .get()
                    .await()

                val chat = ChatUser(it.id, chatMap.getValue("receiverUid").toString(), chatMap.getValue("chatStatus").toString().toInt(), user.toObject(User::class.java)!!)

                chatList.add(chat)
            }

            Result.success(chatList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateChatStatus(idOfferer: String, timeslotId:String, chatId: String): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(idOfferer)
                .collection("timeslots")
                .document(timeslotId)
                .collection("chats")
                .document(chatId)
                .update(mapOf(
                    "chatStatus" to 1,
                ))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    //update reviewState
    suspend fun updateReviewState(userReviewedId: String, timeslotId:String, role: String, oldReviewState: Int): Boolean {
        return try {
            var code = 0
            if(oldReviewState == 0){
                if(role=="Offerer")
                    code = 1
                else
                    code = 2 //receiver
            }
            else
                code = 3
            Firebase.firestore
                .collection("users")
                .document(userReviewedId)
                .collection("timeslots")
                .document(timeslotId)
                .update(mapOf(
                    "reviewState" to code,
                ))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

}