package com.bigosvaap.android.agenda.ui.addeditcontact

import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigosvaap.android.agenda.data.Contact
import com.bigosvaap.android.agenda.data.ContactDao
import com.bigosvaap.android.agenda.ui.ADD_CONTACT_RESULT_OK
import com.bigosvaap.android.agenda.ui.EDIT_CONTACT_RESULT_OK
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val contactDao: ContactDao,
    private val state: SavedStateHandle
) : ViewModel() {

    private val contact = state.get<Contact>("contact")

    var contactName = state.get<String>("contactName") ?: contact?.name ?: ""
        set(value) {
            field = value
            state.set("contactName", value)
        }

    var contactEmail = state.get<String>("contactEmail") ?: contact?.email ?: ""
        set(value) {
            field = value
            state.set("contactEmail", value)
        }

    var contactPhone = state.get<String>("contactPhone") ?: contact?.phone ?: ""
        set(value) {
            field = value
            state.set("contactPhone", value)
        }

    private val addEditContactEventChannel = Channel<AddEditContactEvent>()
    val addEditContactChannel = addEditContactEventChannel.receiveAsFlow()

    private var errorsForm = mutableSetOf<ErrorForms>()

    init{
        isValidName(contactName)
        isValidEmail(contactEmail)
        isValidPhone(contactPhone)
    }

    fun isValidName(name: String): Boolean{
        return if (name.isEmpty()){
            errorsForm.add(ErrorForms.EMPTY_NAME)
            false
        }else{
            errorsForm.remove(ErrorForms.EMPTY_NAME)
            true
        }
    }

    fun isValidEmail(email: String): Boolean{
        if (email.isEmpty())
            return true
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorsForm.add(ErrorForms.INVALID_EMAIL)
            false
        }else{
            errorsForm.remove(ErrorForms.INVALID_EMAIL)
            true
        }
    }

    fun isValidPhone(phone: String): Boolean{
        if (phone.isEmpty())
            return true
        return if (!Patterns.PHONE.matcher(phone).matches()){
            errorsForm.add(ErrorForms.INVALID_PHONE_NUMBER)
            false
        }else{
            errorsForm.remove(ErrorForms.INVALID_PHONE_NUMBER)
            true
        }
    }

    fun onSaveClick(){
        if (errorsForm.isEmpty()){
            if (contact != null){
                val updatedContact = contact.copy(name = contactName, email = contactEmail, phone = contactPhone)
                updateContact(updatedContact)
            }else{
                val newContact = Contact(name = contactName, email = contactEmail, phone = contactPhone)
                createContact(newContact)
            }
        }else
            showInvalidInputMessage()
    }


    private fun createContact(contact: Contact) = viewModelScope.launch {
        try {
            contactDao.insert(contact)
            addEditContactEventChannel.send(AddEditContactEvent.NavigateBackWithResult(
                ADD_CONTACT_RESULT_OK
            ))
        } catch (e: SQLiteConstraintException){
            addEditContactEventChannel.send(AddEditContactEvent.ShowConstraintMessageError)
        }

    }

    private fun updateContact(contact: Contact) = viewModelScope.launch {
        contactDao.update(contact)
        addEditContactEventChannel.send(AddEditContactEvent.NavigateBackWithResult(EDIT_CONTACT_RESULT_OK))
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        addEditContactEventChannel.send(AddEditContactEvent.ShowInvalidFormMessage)
    }


    sealed class AddEditContactEvent{
        object ShowInvalidFormMessage: AddEditContactEvent()
        object ShowConstraintMessageError: AddEditContactEvent()
        data class NavigateBackWithResult(val result: Int): AddEditContactEvent()
    }

    enum class ErrorForms{
        EMPTY_NAME,
        INVALID_EMAIL,
        INVALID_PHONE_NUMBER
    }

}