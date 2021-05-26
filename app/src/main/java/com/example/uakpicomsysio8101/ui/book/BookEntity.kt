package com.example.uakpicomsysio8101.ui.book

import androidx.room.*
import com.fasterxml.jackson.annotation.JsonProperty

@Entity(tableName = "book")
data class BookEntity(
    var query: String? = null,
    var bookId: Long? = null,
    var error: String? = null,
    var language: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var authors: String? = null,
    var publisher: String? = null,
    var isbn13: String? = null,
    var isbn10: String? = null,
    var pages: String? = null,
    var year: String? = null,
    var rating: String? = null,
    var desc: String? = null,
    var price: String? = null,
    var image: String? = null,
    var url: String? = null,
    var pdf: String? = null,
    @PrimaryKey(autoGenerate = true) var id: Long? = null
)