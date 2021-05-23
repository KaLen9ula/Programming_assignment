package com.example.uakpicomsysio8101.ui.photo

import com.example.uakpicomsysio8101.ui.book.Book
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PicturesGist(var total: String, var totalHits: Int, var hits: Array<Hit>)

@JsonClass(generateAdapter = true)
data class Hits(var hits: Array<Hit>)

@JsonClass(generateAdapter = true)
data class Hit(var id: Int, var pageURL: String, var type: String, var tags: String, var previewURL: String, var previewWidth: Int,
               var previewHeight: Int, var webformatURL: String, var webformatWidth: Int, var webformatHeight: Int, var largeImageURL: String, var imageWidth: Int,
               var imageHeight: Int, var imageSize: Int,  var views: Int, var downloads: Int,  var favorites: Int, var likes: Int,
               var comments: Int, var user_id: Int,  var user: String, var userImageURL: String)