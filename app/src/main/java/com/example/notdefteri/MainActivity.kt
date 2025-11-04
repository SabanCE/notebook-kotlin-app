package com.example.notdefteri

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notdefteri.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteRepository: NoteRepository
    private lateinit var notesAdapter: NotlarimAdapter
    private val notesList = mutableListOf<Notlarim>()
    private val allNotesList = mutableListOf<Notlarim>() // To hold the master list of notes

    // Launcher to handle the result from NotDetayActivity
    private val noteDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadNotes() // Reload notes from SharedPreferences and refresh the list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteRepository = NoteRepository(this)

        setupRecyclerView()
        loadNotes()

        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, NotDetayActivity::class.java)
            noteDetailLauncher.launch(intent)
        }

        // SearchBar Filtretmek için gerekli fonksiyonlar
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotes(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    //RecylerView i kurmak için gerekli fonksiyonlar
    private fun setupRecyclerView() {
        notesAdapter = NotlarimAdapter(notesList) { clickedNote ->
            // This block is called when a note is clicked
            val intent = Intent(this, NotDetayActivity::class.java)
            intent.putExtra("not", clickedNote) // Pass the clicked note object
            noteDetailLauncher.launch(intent)
        }
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter
    }
    //Notları yüklemek için gerekli fonksiyonlar
    private fun loadNotes() {
        allNotesList.clear()
        allNotesList.addAll(noteRepository.getAllNotes().sortedByDescending { it.id })
        filterNotes("") // Initially display all notes
    }
    //Notları filtrelemek için gerekli fonksiyonlar
    private fun filterNotes(query: String) {
        val filteredList = if (query.isBlank()) {
            allNotesList
        } else {
            allNotesList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.content.contains(query, ignoreCase = true)
            }
        }
        notesList.clear()
        notesList.addAll(filteredList)
        notesAdapter.notifyDataSetChanged()
    }
}
