package com.example.android.politicalpreparedness.screen.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionListItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ClickListener) :
    ListAdapter<Election, ElectionListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ViewHolder(val binding: ElectionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(election: Election, clickListener: ClickListener) {
            binding.election = election
            binding.executePendingBindings()
            binding.root.setOnClickListener { clickListener.onClick(election) }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ElectionListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ClickListener(var block: (election: Election) -> Unit) {
        fun onClick(election: Election) = block(election)
    }
}