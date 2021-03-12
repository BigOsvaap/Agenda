package com.bigosvaap.android.agenda.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "contacts", indices = [Index(value = ["name", "email", "phone"], unique = true)])
data class Contact(

    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val email: String,
    val phone: String,
)