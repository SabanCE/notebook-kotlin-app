package com.example.notdefteri

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.notdefteri.databinding.ActivityNotDetayBinding

class NotDetayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotDetayBinding
    private lateinit var noteRepository: NoteRepository
    private var currentNote: Notlarim? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotDetayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteRepository = NoteRepository(this)

        // Set up the toolbar
        setSupportActionBar(binding.toolbarNotDetay)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show back button

        currentNote = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("not", Notlarim::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("not") as? Notlarim
        }

        currentNote?.let {
            binding.editTextNoteTitle.setText(it.title)
            binding.editTextNoteContent.setText(it.content)
        }

        // Save button functionality
        binding.fabSaveNote.setOnClickListener {
            saveAndFinish()
        }

        // Handle the system back press to save the note
        onBackPressedDispatcher.addCallback(this) {
            saveAndFinish()
        }
    }

    // This function inflates the menu (adds the delete icon to the toolbar)
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_not_detay, menu)
        // Only show the delete icon if it's an existing note
        val deleteItem = menu.findItem(R.id.action_delete_note)
        deleteItem.isVisible = currentNote != null
        return true
    }

    // This function handles clicks on menu items (e.g., delete or back button)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the toolbar's back button press
                saveAndFinish()
                Toast.makeText(this, "Başarıyla Kaydedildi!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_delete_note -> {
                // Handle the delete icon press
                deleteNoteAndFinish()
                Toast.makeText(this, "Not Silindi!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveAndFinish() {
        val updatedTitle = binding.editTextNoteTitle.text.toString()
        val updatedContent = binding.editTextNoteContent.text.toString()

        if (updatedTitle.isNotBlank() || updatedContent.isNotBlank()) {
            val noteId = currentNote?.id ?: -1 // Use -1 for new notes
            val updatedNote = Notlarim(id = noteId, title = updatedTitle, content = updatedContent)
            noteRepository.saveNote(updatedNote)
        }

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun deleteNoteAndFinish() {
        currentNote?.id?.let {
            noteRepository.deleteNote(it)
        }
        setResult(Activity.RESULT_OK) // Notify MainActivity to refresh
        finish()
    }
}
