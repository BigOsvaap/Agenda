package com.bigosvaap.android.agenda.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bigosvaap.android.agenda.data.Contact
import com.bigosvaap.android.agenda.data.ContactDao
import com.bigosvaap.android.agenda.ui.ADD_CONTACT_RESULT_OK
import com.bigosvaap.android.agenda.ui.DELETE_CONTACT_RESULT_OK
import com.bigosvaap.android.agenda.ui.EDIT_CONTACT_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(private val contactDao: ContactDao) :ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val contactEventChannel = Channel<ContactEvent>()
    val contactEvent = contactEventChannel.receiveAsFlow()

    @ExperimentalCoroutinesApi
    private val contactsFlow = searchQuery.flatMapLatest {
        contactDao.getContacts(it)
    }

    @ExperimentalCoroutinesApi
    val contacts = contactsFlow.asLiveData()

    fun onContactSwiped(contact: Contact) = viewModelScope.launch {
        contactDao.delete(contact)
        contactEventChannel.send(ContactEvent.ShowUndoDeleteContactMessage(contact))
    }

    fun onUndoDeleteContact(contact: Contact) = viewModelScope.launch {
        contactDao.insert(contact)
    }

    fun onAddNewContact() = viewModelScope.launch {
        contactEventChannel.send(ContactEvent.NavigateToAddContactScreen)
    }

    fun onSelectContact(contact: Contact) = viewModelScope.launch {
        contactEventChannel.send(ContactEvent.NavigateToDetailsContactScreen(contact))
    }

    fun onLongSelectContact(contact: Contact) = viewModelScope.launch {
        contactEventChannel.send(ContactEvent.NavigateToOptionsContactDialog(contact))
    }

    fun showContactSavedConfirmationMessage(message: String) = viewModelScope.launch{
        contactEventChannel.send(ContactEvent.ShowContactSavedConfirmationMessage(message))
    }

    fun onContactActionResult(result: Int, contact: Contact?, message: String) {
        when(result){
            DELETE_CONTACT_RESULT_OK -> {
                if (contact != null)
                    viewModelScope.launch {
                        contactEventChannel.send(ContactEvent.ShowUndoDeleteContactMessage(contact))
                    }
            }
            ADD_CONTACT_RESULT_OK -> showContactSavedConfirmationMessage(message)
            EDIT_CONTACT_RESULT_OK -> showContactSavedConfirmationMessage(message)
        }
    }

    sealed class ContactEvent{
        object NavigateToAddContactScreen: ContactEvent()
        data class NavigateToEditContactScreen(val contact: Contact): ContactEvent()
        data class NavigateToDetailsContactScreen(val contact: Contact): ContactEvent()
        data class NavigateToOptionsContactDialog(val contact: Contact): ContactEvent()
        data class ShowUndoDeleteContactMessage(val contact: Contact): ContactEvent()
        data class ShowContactSavedConfirmationMessage(val message: String): ContactEvent()
    }

}