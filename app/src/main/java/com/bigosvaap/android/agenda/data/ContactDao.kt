package com.bigosvaap.android.agenda.data

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: Long): Flow<Contact>

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :searchQuery || '%' ORDER BY name ")
    fun getContacts(searchQuery: String): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Throws(SQLiteConstraintException::class)
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun  update(contact: Contact)

}