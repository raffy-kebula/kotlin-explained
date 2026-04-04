package com.example.myapplication.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.models.Note
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun getAllNotes(): Flow<List<Note>>

    // Flow<List<Note>>: restituisce un Flow di liste di note,
    // cioè una sequenza reattiva che aggiorna automaticamente chi la osserva quando i dati cambiano.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    // suspend è una funzione sospendibile, quindi va chiamata dentro una Coroutine (operazione asincrona).
}