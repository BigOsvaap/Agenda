package com.bigosvaap.android.agenda.data

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Contact::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ContactDao(): ContactDao

    companion object {
        const val DATABASE_NAME = "contact.db"
    }

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().ContactDao()

            GlobalScope.launch(Dispatchers.IO){
                Log.d("AppDatabase", "Callback")
                dao.insert(Contact("Oswaldo", "bigosvaap@gmail.com", "2351012288"))
                dao.insert(Contact("Carlos", "Carlos@gmail.com", "265465461"))
                dao.insert(Contact("Juan", "Juan@gmail.com", "5464546546"))
                dao.insert(Contact("Leyra", "Leyra@gmail.com", "5464546546"))
                dao.insert(Contact("Brando", "Brando@gmail.com", "5464546546"))
            }
        }

    }

}