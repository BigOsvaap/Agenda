package com.bigosvaap.android.agenda.ui.addeditcontact

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bigosvaap.android.agenda.R
import com.bigosvaap.android.agenda.databinding.FragmentContactFormBinding
import com.bigosvaap.android.agenda.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditContactFragment : Fragment(R.layout.fragment_contact_form){

    private val viewModel: AddEditContactViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentContactFormBinding.bind(view)

        binding.apply {
            contactNameEditText.setText(viewModel.contactName)
            contactEmailEditText.setText(viewModel.contactEmail)
            contactPhoneEditText.setText(viewModel.contactPhone)

            //Check name no empty
            if (!viewModel.isValidName(viewModel.contactName))
                contactNameLayout.error = getString(R.string.error_empty)
            else
                contactNameLayout.error = null

            submitForm.setOnClickListener {
                viewModel.onSaveClick()
            }

            contactNameEditText.addTextChangedListener {
                val name = it.toString()
                viewModel.contactName = name
                if (!viewModel.isValidName(name))
                    contactNameLayout.error = getString(R.string.error_empty)
                else
                    contactNameLayout.error = null
            }
            contactEmailEditText.addTextChangedListener {
                val email = it.toString()
                viewModel.contactEmail = email
                if (!viewModel.isValidEmail(email))
                    contactEmailLayout.error = getString(R.string.error_email)
                else
                    contactEmailLayout.error = null

            }
            contactPhoneEditText.addTextChangedListener {
                val phone = it.toString()
                viewModel.contactPhone = phone
                if (!viewModel.isValidPhone(phone))
                    contactPhoneLayout.error = getString(R.string.error_phone)
                else
                    contactPhoneLayout.error = null
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.addEditContactChannel.collect { event ->
                when(event) {
                    is AddEditContactViewModel.AddEditContactEvent.ShowInvalidFormMessage -> {
                         Snackbar.make(requireView(), getText(R.string.error_form), Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditContactViewModel.AddEditContactEvent.NavigateBackWithResult -> {
                        parentFragmentManager.setFragmentResult("contactAction", bundleOf("action" to event.result))
                        val action = AddEditContactFragmentDirections.actionAddEditContactFragmentToContactsFragment()
                        findNavController().navigate(action)
                    }
                    is AddEditContactViewModel.AddEditContactEvent.ShowConstraintMessageError ->{
                        Snackbar.make(requireView(), getString(R.string.error_constraint), Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }


    }


}