package com.example.uakpicomsysio8101.ui.book

import com.fasterxml.jackson.annotation.JsonProperty

class BookNewJson {

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("subtitle")
    var subtitle: String? = null

    @JsonProperty("authors")
    var authors: String? = null

    @JsonProperty("publisher")
    var publisher: String? = null

    @JsonProperty("isbn13")
    var isbn13: String? = null

    @JsonProperty("pages")
    var pages: String? = null

    @JsonProperty("year")
    var year: String? = null

    @JsonProperty("rating")
    var rating: String? = null

    @JsonProperty("desc")
    var desc: String? = null

    @JsonProperty("price")
    var price: String? = null

    @JsonProperty("image")
    var image: String? = null
}