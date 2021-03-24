package com.bigosvaap.android.agenda.ui.dialogoptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigosvaap.android.agenda.data.Contact
import com.bigosvaap.android.agenda.data.ContactDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactDialogViewModel @Inject constructor(private val contactDao: ContactDao): ViewModel() {

    fun onContactDelete(contact: Contact) = viewModelScope.launch {
            contactDao.delete(contact)
    }

    fun onUndoDeleteContact(contact: Contact) = viewModelScope.launch {
        contactDao.insert(contact)
    }

}