package com.bigosvaap.android.agenda.ui.contacts

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigosvaap.android.agenda.R
import com.bigosvaap.android.agenda.data.Contact
import com.bigosvaap.android.agenda.databinding.FragmentContactsBinding
import com.bigosvaap.android.agenda.util.exhaustive
import com.bigosvaap.android.agenda.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private val viewModel: ContactsViewModel by viewModels()
    private val contactsAdapter = ContactsAdapter(
        onItemClick = { contact ->
            viewModel.onSelectContact(contact)
        },
        onLongItemClick = { contact ->
            viewModel.onLongSelectContact(contact)
        }
    )

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentContactsBinding.bind(view)

        binding.apply {

            contactsRecyclerView.apply {
                adapter = contactsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.END or ItemTouchHelper.START
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val contact = contactsAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onContactSwiped(contact)
                }

            }).attachToRecyclerView(contactsRecyclerView)

            fabAddContact.setOnClickListener {
                viewModel.onAddNewContact()
            }

        }

        viewModel.contacts.observe(viewLifecycleOwner) {
            contactsAdapter.submitList(it)
        }

        parentFragmentManager.setFragmentResultListener(
            "contactAction",
            this
        ) { _, bundle ->

            val result = bundle.getInt("action")
            val contact = bundle.get("contactDeleted")
            if(contact != null)
                viewModel.onContactActionResult(result, contact as Contact, "")
            else
                viewModel.onContactActionResult(result, null, getString(R.string.changes_saved))
            Log.d("ContactsFragment", "ResultListener")
            Log.d("ContactsFragment", result.toString())
            Log.d("ContactsFragment", contact.toString())

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.contactEvent.collect { event ->
                when (event) {
                    is ContactsViewModel.ContactEvent.ShowUndoDeleteContactMessage -> {
                        Snackbar.make(requireView(), R.string.deleted_contact, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo) {
                                viewModel.onUndoDeleteContact(event.contact)
                            }.show()
                    }
                    is ContactsViewModel.ContactEvent.NavigateToAddContactScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToAddEditContactFragment(
                                null,
                                getString(R.string.new_contact)
                            )
                        findNavController().navigate(action)
                    }
                    is ContactsViewModel.ContactEvent.NavigateToEditContactScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToAddEditContactFragment(
                                event.contact,
                                getString(R.string.edit_contact)
                            )
                        findNavController().navigate(action)
                    }
                    is ContactsViewModel.ContactEvent.NavigateToDetailsContactScreen -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToDetailsContact(
                                event.contact
                            )
                        findNavController().navigate(action)
                    }
                    is ContactsViewModel.ContactEvent.NavigateToOptionsContactDialog -> {
                        val action =
                            ContactsFragmentDirections.actionContactsFragmentToContactDialogFragment(
                                event.contact
                            )
                        findNavController().navigate(action)
                    }
                    is ContactsViewModel.ContactEvent.ShowContactSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_contacts, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

    }

}