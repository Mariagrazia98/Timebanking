package it.polito.timebanking.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
}