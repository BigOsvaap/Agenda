package com.bigosvaap.android.agenda.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contacts", indices = [Index(value = ["name"], unique = true)])
data class Contact(

    val name: String,
    val email: String,
    val phone: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0

) : Parcelable