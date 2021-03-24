package com.bigosvaap.android.agenda.ui.dialogoptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bigosvaap.android.agenda.R
import com.bigosvaap.android.agenda.databinding.FragmentContactDialogBinding
import com.bigosvaap.android.agenda.ui.DELETE_CONTACT_RESULT_OK
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDialogFragment() : BottomSheetDialogFragment() {

    private val viewModel: ContactDialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentContactDialogBinding.bind(view)

        val args: ContactDialogFragmentArgs by navArgs()

        with(binding) {

            nameContact.text = args.contact.name

            detailsContact.setOnClickListener {
                val action =
                    ContactDialogFragmentDirections.actionContactDialogFragmentToDetailsContact(args.contact)
                findNavController().navigate(action)
            }
            editContact.setOnClickListener {
                val action =
                    ContactDialogFragmentDirections.actionContactDialogFragmentToAddEditContactFragment(
                        args.contact, getString(
                            R.string.edit_contact
                        )
                    )
                findNavController().navigate(action)
            }
            deleteContact.setOnClickListener {
                viewModel.onContactDelete(args.contact)
                parentFragmentManager.setFragmentResult(
                    "contactAction",
                    bundleOf("action" to DELETE_CONTACT_RESULT_OK, "contactDeleted" to args.contact)
                )
                dismiss()
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentContactDialogBinding.inflate(inflater, container, false).root
    }

}