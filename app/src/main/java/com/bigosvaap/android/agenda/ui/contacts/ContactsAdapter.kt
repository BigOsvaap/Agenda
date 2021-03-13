package com.bigosvaap.android.agenda.ui.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bigosvaap.android.agenda.data.Contact
import com.bigosvaap.android.agenda.databinding.ContactItemBinding

class ContactsAdapter : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(DiffCallback()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    class ContactViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(contact: Contact){
            binding.apply {
                contactName.text = contact.name
            }
        }

    }



    class DiffCallback : DiffUtil.ItemCallback<Contact>() {

        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }

    }

}