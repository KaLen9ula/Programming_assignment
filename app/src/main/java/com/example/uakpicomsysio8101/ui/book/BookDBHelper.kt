package com.example.uakpicomsysio8101.ui.book

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import kotlin.random.Random


class BookDBHelper(context: Context) {

    init {
        BookDBHelper.context = WeakReference(context)
        createDatabase()
    }

    companion object {

        private var context: WeakReference<Context>? = null
        private const val DATABASE_NAME: String = "book"
        private var singleton: BookDB? = null

        private fun createDatabase(): BookDB {
            return Room.databaseBuilder(
                context?.get()
                    ?: throw IllegalStateException("initialize by calling constructor before calling BookDB.instance"),
                BookDB::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }


        val instance: BookDB
            @Synchronized get() {
                if (null == singleton)
                    singleton = createDatabase()

                return singleton as BookDB
            }
    }
}