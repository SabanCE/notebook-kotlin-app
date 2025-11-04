package com.example.notdefteri

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notdefteri.databinding.ItemNoteBinding

class NotlarimAdapter(
    private val notlarList: List<Notlarim>,
    private val onItemClick: (Notlarim) -> Unit // Callback to handle item clicks
) : RecyclerView.Adapter<NotlarimAdapter.NotlarimViewHolder>() {

    inner class NotlarimViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotlarimViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotlarimViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotlarimViewHolder, position: Int) {
        val not = notlarList[position]
        holder.binding.itemNoteTitle.text = not.title
        holder.binding.itemNoteContent.text = not.content

        // When an item is clicked, invoke the callback with the clicked note
        holder.itemView.setOnClickListener {
            onItemClick(not)
        }
    }

    override fun getItemCount(): Int {
        return notlarList.size
    }
}
