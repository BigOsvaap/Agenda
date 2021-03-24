package com.bigosvaap.android.agenda.ui.detailscontact

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bigosvaap.android.agenda.R
import com.bigosvaap.android.agenda.databinding.FragmentContactDetailsBinding
import com.bigosvaap.android.agenda.ui.DELETE_CONTACT_RESULT_OK
import com.bigosvaap.android.agenda.ui.dialogoptions.ContactDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsContact : Fragment(R.layout.fragment_contact_details){

    private val viewModel: ContactDialogViewModel by viewModels()
    private val args: DetailsContactArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentContactDetailsBinding.bind(view)

        with(binding){

            contactName.text = getString(R.string.show_contact_name, args.contact.name)
            contactEmail.text = getString(R.string.show_contact_email, args.contact.email)
            contactPhone.text = getString(R.string.show_contact_phone, args.contact.phone)

        }

        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.editContact -> {
                val action =
                   DetailsContactDirections.actionDetailsContactToAddEditContactFragment(
                        args.contact,
                        getString(R.string.edit_contact)
                    )
                findNavController().navigate(action)
                true
            }
            R.id.deleteContact ->{
                viewModel.onContactDelete(args.contact)
                parentFragmentManager.setFragmentResult(
                    "contactAction",
                    bundleOf("action" to DELETE_CONTACT_RESULT_OK, "contactDeleted" to args.contact)
                )
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}