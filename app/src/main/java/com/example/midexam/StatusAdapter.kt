package com.example.midexam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class StatusAdapter(
    private val list: MutableList<Status>,
    private val onEditClick: (Status) -> Unit,
    private val onDeleteClick: (Status) -> Unit
) : RecyclerView.Adapter<StatusAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStatus: TextView = itemView.findViewById(R.id.tvItemStatus)
        val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_status, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val status = list[position]
        holder.tvStatus.text = status.text

        holder.btnEdit.setOnClickListener { onEditClick(status) }
        holder.btnDelete.setOnClickListener { onDeleteClick(status) }
    }

    override fun getItemCount(): Int = list.size
}





