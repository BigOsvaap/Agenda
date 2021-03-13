package com.bigosvaap.android.agenda.ui.contacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigosvaap.android.agenda.R
import com.bigosvaap.android.agenda.databinding.FragmentContactsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private val viewModel: ContactsViewModel by viewModels()
    private val contactsAdapter = ContactsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentContactsBinding.bind(view)

        binding.apply {
            contactsRecyclerView.apply {
                adapter = contactsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.contacts.observe(viewLifecycleOwner){
            contactsAdapter.submitList(it)
        }

    }

}