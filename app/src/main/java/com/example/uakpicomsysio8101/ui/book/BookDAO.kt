package com.example.uakpicomsysio8101.ui.book

import androidx.room.*

@Dao
interface BookDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertByReplacementBooks(books: List<BookEntity>)

    @Query("DELETE FROM book")
    suspend fun nukeBooks()

    @Query("SELECT * FROM book")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM book WHERE `query` = :query")
    suspend fun getByQuery(query: String): List<BookEntity>

    @Query("SELECT * FROM book WHERE isbn13 = :isbn13")
    suspend fun getByISBN(isbn13: String): BookEntity
}