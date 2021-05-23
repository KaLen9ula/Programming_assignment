package com.example.uakpicomsysio8101.ui.book

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookAdapter(var error: String, var total: String, var page: String, var books: List<Book>)
