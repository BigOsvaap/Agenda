package com.bigosvaap.android.agenda.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bigosvaap.android.agenda.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Contact::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ContactDao(): ContactDao

    companion object {
        const val DATABASE_NAME = "contact.db"
    }

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().ContactDao()

            applicationScope.launch {
                dao.insert(Contact("Oswaldo", "oswaldo@gmail.com", "2351012288"))
                dao.insert(Contact("Carlos", "Carlos@gmail.com", "265465461"))
                dao.insert(Contact("Juan", "Juan@gmail.com", "5464546546"))
            }
        }

    }

}