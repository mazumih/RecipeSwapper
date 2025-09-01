package com.example.recipeswapper.data.repositories

import android.util.Log
import androidx.room.util.copy
import com.example.recipeswapper.data.database.EventDao
import com.example.recipeswapper.data.database.EventEntity
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.models.Recipe
import com.example.recipeswapper.data.models.User
import com.example.recipeswapper.data.models.toDomain
import com.example.recipeswapper.data.models.toEntity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class EventsRepository(
    private val firestore: FirebaseFirestore,
    private val dao: EventDao
) {

    fun getAllEvents(): Flow<List<Event>> = dao.getAllEvents().map {
        it.map { event ->
            event.toDomain()
        }
    }

    suspend fun getEventsDB() {
        val collection = firestore.collection("Events").get().await()

        collection.documents.forEach { e ->
            val event = e.toObject(EventEntity::class.java)?.copy(id = e.id)
            if (event != null) dao.upsertEvent(event)
        }
    }

    suspend fun addEvent(event: Event, host: String) {
        val document = firestore.collection("Events").document()
        val newEvent = event.copy(id = document.id, host = host)

        document.set(newEvent).await()
        dao.upsertEvent(newEvent.toEntity())
    }

    suspend fun deleteEvent(event: Event) {
        firestore.collection("Events").document(event.id).delete().await()
        dao.deleteEvent(event.toEntity())
    }

    suspend fun addParticipant(event: Event, userId: String) {
        val document = firestore.collection("Events").document(event.id)
        val snapshot =  firestore.collection("Events").document(event.id).get().await()
        val currEvent = snapshot.toObject(Event::class.java)?.copy(id = document.id)

        if (currEvent != null) {
            Log.d("EventCheck", "Partecipanti attuali: ${currEvent.participants.size}, max: ${currEvent.maxParticipants}")
        }

        if (currEvent != null) {
            if (currEvent.participants.contains(userId)) {
                document.update("participants", FieldValue.arrayRemove()).await()
            } else {
                if (currEvent.participants.size < currEvent.maxParticipants) {
                    document.update("participants", FieldValue.arrayUnion(userId)).await()
                }
            }
        }

        val currentEvent = dao.getEvent(event.id) ?: return
        val updatedEvent = when {
            currentEvent.participants.contains(userId) ->
                currentEvent.copy(participants = currentEvent.participants - userId)
            currentEvent.participants.size < currentEvent.maxParticipants ->
                currentEvent.copy(participants = currentEvent.participants + userId)
            else -> currentEvent
        }
        dao.upsertEvent(updatedEvent)
    }

    suspend fun getUserEvents(userId: String) : List<Event> {
        return dao.getUserEvents(userId).map { e -> e.toDomain() }
    }

}