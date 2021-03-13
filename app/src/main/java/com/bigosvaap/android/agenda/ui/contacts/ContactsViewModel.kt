package com.bigosvaap.android.agenda.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bigosvaap.android.agenda.data.ContactDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(private val contactDao: ContactDao) :ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val contactsFlow = searchQuery.flatMapLatest {
        contactDao.getContacts(it)
    }

    val contacts = contactsFlow.asLiveData()

}