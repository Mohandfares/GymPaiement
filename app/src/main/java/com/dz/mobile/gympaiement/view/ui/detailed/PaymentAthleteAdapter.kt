package com.dz.mobile.gympaiement.view.ui.detailed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dz.mobile.gympaiement.data.bo.Payment
import com.dz.mobile.gympaiement.databinding.PaymentItemBinding
import com.dz.mobile.gympaiement.view.Ext.toStringFormat

class PaymentAthleteAdapter : ListAdapter<Payment,PaymentAthleteAdapter.PaymentViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val binding = PaymentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class PaymentViewHolder(private val binding: PaymentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Payment) {
            binding.apply {
                montant.text = "${item.amount.toInt()} DZD"
                date.text = item.date?.toStringFormat()
                typepayment.text = item.type
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Payment>() {
        override fun areItemsTheSame(oldItem: Payment, newItem: Payment): Boolean =
            oldItem.idPayment == newItem.idPayment

        override fun areContentsTheSame(oldItem: Payment, newItem: Payment): Boolean =
            oldItem == newItem
    }
}