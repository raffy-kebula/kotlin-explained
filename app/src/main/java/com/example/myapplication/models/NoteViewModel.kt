package com.example.myapplication.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dao.NoteDao
import kotlinx.coroutines.launch

class NoteViewModel(private val dao: NoteDao) : ViewModel() {

    // Flow dal DB convertito in LiveData
    val allNotes: LiveData<List<Note>> = dao.getAllNotes().asLiveData()

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            dao.insert(Note(title = title, content = content))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            dao.delete(note)
        }
    }
}