package com.example.recipeswapper.data.repositories

import com.example.recipeswapper.data.database.EventDao
import com.example.recipeswapper.data.database.EventEntity
import com.example.recipeswapper.data.models.Event
import com.example.recipeswapper.data.models.toDomain
import com.example.recipeswapper.data.models.toEntity
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

    suspend fun getUserEvents(userId: String) : List<Event> {
        return dao.getUserEvents(userId).map { e -> e.toDomain() }
    }

}