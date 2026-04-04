package com.example.myapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.dao.NoteDao
import com.example.myapplication.models.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    // Serve per ottenere l’istanza del DAO (NoteDao) collegato al database.
    // Room genererà automaticamente il codice per restituire l’oggetto DAO funzionante.
    abstract fun noteDao(): NoteDao

    // companion object: contiene variabili e funzioni condivise da tutte le istanze della classe.
    // @Volatile: assicura che l’istanza del database sia visibile correttamente a tutti i thread (thread-safe).
    // INSTANCE memorizza l’unica istanza del database.

    companion object {
        @Volatile private var INSTANCE: NoteDatabase? = null  // singleton

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context, NoteDatabase::class.java, "note_db"
                ).build().also { INSTANCE = it }
            } // salva l’istanza appena creata in INSTANCE usando una funzione di scoping (also).
              // it = l’oggetto appena creato (NoteDatabase).
        }
    }
}