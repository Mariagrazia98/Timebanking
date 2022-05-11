package it.polito.timebanking.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.model.UserFire
import kotlinx.coroutines.tasks.await

class TimeSlotRepository(application: Application) {
    private val slotDao = SlotDatabase.getDatabase(application)?.slotDao()
    private val db = FirebaseFirestore.getInstance()

    //slots
    fun addSlot(title:String,description:String,date:String,time:String,duration: Int, location: String):Long?{
        val s = Slot().also{ it.title=title; it.description=description; it.date=date; it.time=time; it.duration=duration; it.location=location}
        val id_returned:Long? = slotDao?.addSlot(s)
        return id_returned
    }

    fun updateSlot(slot:Slot){
        slotDao?.updateSlot(slot)
    }

    fun clearAllSlots(){
        slotDao?.removeAll()
    }

    fun removeSlotById(id: String){
        slotDao?.removeSlotById(id)
    }

    fun getAllSlots(): LiveData<List<Slot>>? = slotDao?.findAll()

    fun getSlotById(id: Long?): LiveData<Slot>? = slotDao?.searchSlotByID(id)



    //Firebase
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

    suspend fun removeSlotF(userId: String, slotId: String): Boolean {
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

    suspend fun updateSlotF(userId: String, slot: TimeSlotFire): Boolean {
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

    suspend fun getAllSlotsByUser(uid: String): Result<List<TimeSlotFire>> {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("timeslots")
                .get()
                .await()

            print(data.documents.map { it.data.toString() })

            val result = mutableListOf<TimeSlotFire>()
            data.documents.map {
                if(it.data != null){
                    it.toObject(TimeSlotFire::class.java)?.let { it1 -> result.add(it1) }
                }
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSlotFById(uid: String, slotId: String): Result<TimeSlotFire?> {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("timeslots")
                .document(slotId)
                .get()
                .await()

            Result.success(data.toObject(TimeSlotFire::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSkills(): Result<List<String>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .get()
                .await()

            val skills = mutableListOf<String>()
            users.forEach {
                val timeslots = Firebase.firestore
                    .collection("users")
                    .document(it.id)
                    .collection("timeslots")
                    .get()
                    .await()
                timeslots.documents.map {
                    if(it.data != null){
                        it.toObject(TimeSlotFire::class.java)?.let { it1 ->
                            it1.skills.forEach{it2 ->
                                skills.add(it2)}
                        }
                    }
                }
            }
            return Result.success(skills)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getSlotsBySkill(userId: String, skill: String): Result<List<TimeSlotFire>> {
        try {
            val users = Firebase.firestore
                .collection("users")
                .get()
                .await()

            val filteredSlots = mutableListOf<TimeSlotFire>()
            users.forEach { user ->
                user.toObject(UserFire::class.java).let { u ->
                    Log.d("user", u.uid)
                    Log.d("userId", userId)
                    if (u.uid != userId) {
                        val timeslots = Firebase.firestore
                            .collection("users")
                            .document(u.uid)
                            .collection("timeslots")
                            .get()
                            .await()
                        timeslots.documents.map {
                            if (it.data != null) {
                                it.toObject(TimeSlotFire::class.java)?.let { it1 ->
                                    if (it1.skills.contains(skill)) {
                                        filteredSlots.add(it1)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return Result.success(filteredSlots)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}