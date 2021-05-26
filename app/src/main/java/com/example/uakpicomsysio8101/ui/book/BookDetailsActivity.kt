package com.example.uakpicomsysio8101.ui.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import android.widget.ImageView
import android.widget.TextView
import com.example.uakpicomsysio8101.R
import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.reflect.Field

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var info: BookNewJson
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var authors: TextView
    private lateinit var publisher: TextView
    private lateinit var pages: TextView
    private lateinit var year: TextView
    private lateinit var rating: TextView
    private lateinit var desc: TextView
    private lateinit var price: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)
        init()
    }

    fun init() {
        try {
            val mapper = ObjectMapper()
            val jsonText = intent.extras!!["info"].toString()
            info = mapper.readValue(jsonText, BookNewJson::class.java)
            image = findViewById(R.id.image)
            Glide.with(this).load(info.image).into(image)

            year = findViewById(R.id.year)
            year.text = year.text.toString() + " " + info.year
            subtitle = findViewById(R.id.subtitle)
            subtitle.text = subtitle.text.toString() + " " + info.subtitle
            publisher = findViewById(R.id.publisher)
            publisher.text = publisher.text.toString() + " " + info.publisher
            desc = findViewById(R.id.desc)
            desc.text = desc.text.toString() + " " + info.desc
            price = findViewById(R.id.price)
            price.text = price.text.toString() + " " + info.price
            authors = findViewById(R.id.authors)
            authors.text = authors.text.toString() + " " + info.authors
            pages = findViewById(R.id.pages)
            pages.text = pages.text.toString() + " " + info.pages
            title = findViewById(R.id.title)
            title.text = title.text.toString() + " " + info.title
            title = findViewById(R.id.rating)
            title.text = title.text.toString() + " " + info.rating
        } catch (e: Exception) {
            val mapper = ObjectMapper()
            val jsonText = intent.extras!!["info"].toString()
            info = mapper.readValue(jsonText, BookNewJson::class.java)
            year = findViewById(R.id.year)
            year.text = year.text.toString() + " " + info.year
            subtitle = findViewById(R.id.subtitle)
            subtitle.text = subtitle.text.toString() + " " + info.subtitle
            publisher = findViewById(R.id.publisher)
            publisher.text = publisher.text.toString() + " " + info.publisher
            desc = findViewById(R.id.desc)
            desc.text = desc.text.toString() + " " + info.desc
            price = findViewById(R.id.price)
            price.text = price.text.toString() + " " + info.price
            authors = findViewById(R.id.authors)
            authors.text = authors.text.toString() + " " + info.authors
            pages = findViewById(R.id.pages)
            pages.text = pages.text.toString() + " " + info.pages
            title = findViewById(R.id.title)
            title.text = title.text.toString() + " " + info.title
            title = findViewById(R.id.rating)
            title.text = title.text.toString() + " " + info.rating
        }
    }

    fun getResId(resName: String, c: Class<*>): Int {
        return try {
            val idField: Field = c.getDeclaredField(resName)
            idField.getInt(idField)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            -1
        }
    }
}