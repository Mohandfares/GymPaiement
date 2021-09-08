package com.dz.mobile.gympaiement.view.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.databinding.AthleteItemBinding

class AthleteAdapter(private val listener: OnItemClickListener) : ListAdapter<Athlete, AthleteAdapter.AthleteViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AthleteViewHolder {
        val binding = AthleteItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AthleteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AthleteViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class AthleteViewHolder(private val binding: AthleteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                val position = layoutPosition
                if (position != RecyclerView.NO_POSITION) {
                    raw.setOnClickListener {
                        listener.onItemClick(getItem(position))
                    }
                }
            }
        }
        fun bind(athlete: Athlete) {
            binding.apply {
                name.text = athlete.firstAndLastName
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Athlete>() {
        override fun areItemsTheSame(oldItem: Athlete, newItem: Athlete): Boolean =
            oldItem.idAthlete == newItem.idAthlete

        override fun areContentsTheSame(oldItem: Athlete, newItem: Athlete): Boolean =
            oldItem == newItem
    }

    fun interface OnItemClickListener {
        fun onItemClick(athlete: Athlete)
    }
}