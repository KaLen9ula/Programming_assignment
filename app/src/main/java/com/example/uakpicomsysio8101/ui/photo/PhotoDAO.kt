package com.example.uakpicomsysio8101.ui.photo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PhotoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertByReplacement(image: List<PhotoEntity>)

    @Query("DELETE FROM image")
    suspend fun nukeTable()

    @Insert
    suspend fun insertAll(articleList : List<PhotoEntity>) : List<Long>

    @Query("SELECT * FROM image")
    suspend fun getAll(): List<PhotoEntity>
}