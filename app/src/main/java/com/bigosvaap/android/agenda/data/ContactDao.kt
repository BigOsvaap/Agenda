package com.bigosvaap.android.agenda.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: Long): Flow<Contact>

    @Query("SELECT * FROM contacts")
    fun getAllContacts(): Flow<List<Contact>>

    @Insert
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun  update(contact: Contact)

}