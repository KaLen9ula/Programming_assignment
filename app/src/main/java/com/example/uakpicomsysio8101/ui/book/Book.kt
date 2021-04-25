package com.example.uakpicomsysio8101.ui.book

import com.fasterxml.jackson.annotation.JsonProperty

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
}