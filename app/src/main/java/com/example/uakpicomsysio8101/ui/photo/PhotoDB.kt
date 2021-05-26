package com.example.uakpicomsysio8101.ui.photo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PhotoEntity::class], version = 1)
abstract class PictureDB : RoomDatabase() {
    abstract fun getImageTestDao(): PhotoDAO
}