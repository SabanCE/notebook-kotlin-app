package com.example.notdefteri

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class NoteRepository(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)

    fun getAllNotes(): MutableList<Notlarim> {
        val notes = mutableListOf<Notlarim>()
        val jsonString = sharedPreferences.getString("notes_list", null)

        if (jsonString != null) {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("id")
                val title = jsonObject.getString("title")
                val content = jsonObject.getString("content")
                notes.add(Notlarim(id, title, content))
            }
        }
        return notes
    }

    fun saveNote(note: Notlarim) {
        val notes = getAllNotes()
        val existingNoteIndex = notes.indexOfFirst { it.id == note.id }

        if (existingNoteIndex != -1) {
            // Update existing note
            notes[existingNoteIndex] = note
        } else {
            // Add new note
            val newId = (notes.maxByOrNull { it.id }?.id ?: 0) + 1
            notes.add(note.copy(id = newId))
        }

        saveNotesList(notes)
    }

    fun deleteNote(noteId: Int) {
        val notes = getAllNotes()
        notes.removeAll { it.id == noteId }
        saveNotesList(notes)
    }

    private fun saveNotesList(notes: List<Notlarim>) {
        val jsonArray = JSONArray()
        for (note in notes) {
            val jsonObject = JSONObject()
            jsonObject.put("id", note.id)
            jsonObject.put("title", note.title)
            jsonObject.put("content", note.content)
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit().putString("notes_list", jsonArray.toString()).apply()
    }
}
