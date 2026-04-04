package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.db.NoteDatabase
import com.example.myapplication.models.Note
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Layout edge-to-edge (la UI si estende sotto la status bar e navigation bar)
        enableEdgeToEdge()

        // Ottieni l'istanza singleton del database Room
        val db = NoteDatabase.getDatabase(this)

        // Lancia una coroutine nel lifecycle scope dell'Activity.
        lifecycleScope.launch {

            // si SOSPENDE qui (non blocca il thread), il main thread è libero di disegnare la UI
            // quando i dati arrivano, RIPRENDE da qui
            val existingNotes = db.noteDao().getAllNotes().first()

            // Se il database è vuoto (primo avvio), inserisce le note di esempio
            if (existingNotes.size != 4) {

                val initialNotes = listOf(
                    Note(title = "Prima nota",   content = "Ciao!"),
                    Note(title = "Seconda nota", content = "Benvenuto in Room"),
                    Note(title = "Terza nota",   content = "Compose è potente"),
                    Note(title = "Quarta nota",  content = "Kotlin è divertente")
                )

                val existingTitles = existingNotes.map { it.title }

                val missingNotes = initialNotes.filter { it.title !in existingTitles }

                missingNotes.forEach { db.noteDao().insert(it) }
            }
        }

        // Imposta il contenuto della UI
        setContent {
            MyApplicationTheme {

                // Stato locale che contiene la lista di note da mostrare.
                // 'remember' fa sì che il valore sopravviva alle recomposition.
                // 'mutableStateOf' notifica Compose quando il valore cambia,
                // provocando un ridisegno automatico della UI.
                var notes by remember { mutableStateOf(listOf<Note>()) }

                // LaunchedEffect si avvia una sola volta
                // e rimane attivo per tutto il ciclo di vita del composable.
                // Osserva il Flow in modo continuo: ogni volta che il DB cambia,
                // 'notes' viene aggiornato e la UI si ridisegna automaticamente.
                LaunchedEffect(Unit) {
                    db.noteDao().getAllNotes().collectLatest { list ->
                        notes = list
                    }
                }

                // Scaffold fornisce la struttura base dell'app
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // Column dispone la lista e il pulsante in verticale
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {

                        // Passa la lista di note al composable che le visualizza
                        NotesList(
                            notes = notes,
                            modifier = Modifier.weight(1f)
                        )

                        // Pulsante per eliminare l'ultima nota della lista
                        Button(
                            onClick = {

                                lifecycleScope.launch {

                                    // lastOrNull() evita crash se la lista è vuota:
                                    // restituisce null invece di lanciare un'eccezione
                                    notes.lastOrNull()?.let { lastNote ->

                                        // Elimina la nota dal database tramite il DAO
                                        // il Flow aggiornerà automaticamente la UI
                                        db.noteDao().delete(lastNote)
                                    }
                                }
                            },
                            // Disabilita il pulsante se non ci sono note da eliminare
                            enabled = notes.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text("Elimina ultima nota")
                        }

                        // Pulsante per aprire la schermata della posizione
                        Button(
                            onClick = {
                                // Crea un Intent per avviare LocationActivity
                                val intent = Intent(this@MainActivity, LocationActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
                        ) {
                            Text("Apri posizione")
                        }
                    }
                }
            }
        }
    }
}

// Composable che mostra la lista delle note in modo scorrevole
@Composable
fun NotesList(notes: List<Note>, modifier: Modifier = Modifier) {

    // LazyColumn renderizza solo gli elementi visibili a schermo, ottimizzando le performance
    LazyColumn(modifier = modifier) {

        // 'items' itera sulla lista e crea un composable per ogni nota
        items(notes) { note ->

            // Mostra titolo e contenuto della nota come testo
            Text(
                text = "${note.title}: ${note.content}",
                style = MaterialTheme.typography.bodyLarge, // stile tipografico del tema
                modifier = Modifier.padding(8.dp)      // margine interno attorno al testo
            )
        }
    }
}