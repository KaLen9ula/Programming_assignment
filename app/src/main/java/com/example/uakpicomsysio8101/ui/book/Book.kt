package com.example.uakpicomsysio8101.ui.book

import com.fasterxml.jackson.annotation.JsonProperty
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Book {

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("subtitle")
    var subtitle: String? = null

    @JsonProperty("isbn13")
    var isbn13: String? = null

    @JsonProperty("price")
    var price: String? = null

    @JsonProperty("image")
    var image: String? = null

    @JsonProperty("url")
    var url: String? = null

    override fun toString(): String {
        return "Book(title=$title, subtitle=$subtitle, isbn13=$isbn13, price=$price, image=$image, url=${url})"
    }
}