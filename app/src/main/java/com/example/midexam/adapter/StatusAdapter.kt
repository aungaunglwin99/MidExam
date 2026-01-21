package com.example.midexam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.midexam.databinding.ListItemStatusBinding
import com.example.midexam.model.StatusModel

class StatusAdapter(
    private val list: MutableList<StatusModel>,
    private val onEditClick: (StatusModel) -> Unit,
    private val onDeleteClick: (StatusModel) -> Unit
) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {

    class StatusViewHolder(val binding: ListItemStatusBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StatusViewHolder(
        ListItemStatusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) = with(holder.binding) {
        val status = list[position]
        tvItemStatus.text = status.text
        ivEdit.setOnClickListener { onEditClick(status) }
        ivDelete.setOnClickListener { onDeleteClick(status) }
    }

    override fun getItemCount(): Int = list.size
}