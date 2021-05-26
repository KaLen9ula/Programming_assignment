package com.example.uakpicomsysio8101.ui.book

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.IOException
import com.fasterxml.jackson.annotation.JsonProperty
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uakpicomsysio8101.R
import kotlinx.coroutines.runBlocking
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import java.lang.reflect.Field


class BookFragment : Fragment() {
    private var columnCount = 1
    private var bookContainer: BookContainer = BookContainer()
    private lateinit var view: LinearLayout

    private lateinit var adapter: ViewAdapterRecycler
    private lateinit var searchField: EditText
    private lateinit var addButton: Button

    private lateinit var dao: BookDAO
    private lateinit var dbHelper: BookDBHelper

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        view = inflater.inflate(R.layout.fragment_book_list, container, false) as LinearLayout
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(view.getChildAt(1) as RecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
        }
        dbHelper = BookDBHelper(requireActivity())
        dao = BookDBHelper.instance.getBookDAO()

        initSearchField()
        val arr = bookContainer.search

        enableSwipeToDeleteAndUndo()
        addButton = requireView().findViewById(R.id.add)
        addButton.setOnClickListener {
            withEditText(addButton)
        }
    }


    fun withEditText(view: View) {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        builder.setTitle("Add new book")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
        val title = dialogLayout.findViewById<EditText>(R.id.title)
        val subtitle = dialogLayout.findViewById<EditText>(R.id.subtitle)
        var price = dialogLayout.findViewById<EditText>(R.id.price)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Add") { dialogInterface, i ->
            run {
                val search = Book()
                search.title = title.text.toString()
                search.subtitle = subtitle.text.toString()
                search.price = price.text.toString()
                bookContainer.search.add(search)
                dialogInterface.cancel()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()
    }


    private fun initBook(books: List<Book>, query: String) {
        bookContainer.search = books.toMutableList()
        adapter = ViewAdapterRecycler(requireContext(), books) {
            val client = OkHttpClient()

            val request = Request.Builder()
                    .url("https://api.itbook.store/1.0/books/${it.isbn13!!.replace("a", "")}")
                    .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }

                val intent = Intent(requireContext(), BookDetailsActivity::class.java)
                intent.putExtra("info", response.body!!.string())
                startActivity(intent)
            }
        }
        view.findViewById<RecyclerView>(R.id.list).adapter = adapter
    }


    private fun initSearchField() {
        searchField = requireView().findViewById(R.id.filter)
        searchField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 2) {
                    if (runBlocking { dao.getByQuery(s.toString()).isEmpty()}) {
                        try {
                            downloadData(s.toString())
                        } catch (e: java.lang.Exception) {
                            println(e.message)
                            Toast.makeText(
                                requireContext(),
                                "No internet connection and database is empty!",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {
                        val bookEntities = runBlocking { dao.getByQuery(s.toString()) }
                        val arrOfBooks = fromBookEntityListToBookList(bookEntities)
                        adapter = ViewAdapterRecycler(requireContext(), arrOfBooks) {
                            val objectMapper = ObjectMapper()
                            val entity = runBlocking { dao.getByISBN(it.isbn13!!.replace("a", "")) }
                            val json = objectMapper.writeValueAsString(fromBookEntityToDetails(entity))
                            val intent = Intent(requireContext(), BookDetailsActivity::class.java)
                            intent.putExtra("info", json)
                            startActivity(intent)
                        }
                        view.findViewById<RecyclerView>(R.id.list).adapter = adapter
                    }
                }
            }
        })
    }
    private fun downloadData(url: String) {
        val client = OkHttpClient()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val gistJsonAdapter = moshi.adapter(BookAdapter::class.java)

        val request = Request.Builder()
            .url("https://api.itbook.store/1.0/search/${url}")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val gist = gistJsonAdapter.fromJson(response.body!!.source())
            val arrOfBooks = ArrayList<BookEntity>()
            gist!!.books.forEach {x -> arrOfBooks.add(fromBookToBookEntity(x))}
            saveToDB(gist, url)
            initBook(gist.books, url)
        }
    }

    fun saveToDB(gist: BookAdapter, query: String) {
        gist.books.forEach { x ->
            run {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.itbook.store/1.0/books/${x.isbn13!!.replace("a", "")}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    for ((name, value) in response.headers) {
                        println("$name: $value")
                    }

                    val mapper = ObjectMapper()
                    val jsonText = response.body!!.string()
                    val details = mapper.readValue(jsonText, BookNewJson::class.java)
                    val entity = fromDetailsToBookEntity(details)
                    entity.query = query
                    runBlocking { dao.upsertByReplacementBooks(listOf(entity)) }
                    println("test " + runBlocking { dao.getAllBooks() })
                }
            }
        }}

    fun filter(text: String) {
        val temp: MutableList<Book> = ArrayList()
        for (search in bookContainer.search) {
            if (search.title!!.contains(text)) {
                temp.add(search)
            }
        }
        adapter.updateList(temp)
    }

    private fun displaySnackBarWithBottomMargin(
            snackbar: Snackbar,
            sideMargin: Int,
            marginBottom: Int
    ) {
        val snackBarView = snackbar.view
        val params = snackBarView.layoutParams as CoordinatorLayout.LayoutParams
        params.setMargins(
                params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + marginBottom
        )
        snackBarView.layoutParams = params
        snackbar.show()
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDelete = object : SwipeToDelete(
                requireContext()
        ) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                try {
                    val position = viewHolder.adapterPosition
                    val item: Book = adapter.getData().get(position)
                    val suppData: Book = bookContainer.search[position]
                    adapter.removeItem(position)
                    bookContainer.search.removeAt(position)
                    val snackbar: Snackbar = Snackbar
                            .make(
                                    view,
                                    "Book was deleted!",
                                    Snackbar.LENGTH_LONG
                            )
                    snackbar.setAction("Undo", View.OnClickListener {
                        adapter.restoreItem(item, position)
                        bookContainer.search.add(position, suppData)
                        view.findViewById<RecyclerView>(R.id.list).scrollToPosition(position)
                    })
                    snackbar.setActionTextColor(Color.WHITE)
                    displaySnackBarWithBottomMargin(snackbar, 40, 40)
                } catch (ignored: Exception) {
                }
            }
        }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(view.findViewById<RecyclerView>(R.id.list))
    }

    private fun fromBookEntityToBook(bookEntity: BookEntity): Book {
        val book = Book()
        book.run {
            isbn13 = bookEntity.isbn13
            image = bookEntity.image
            url = bookEntity.url
            price = bookEntity.price
            title = bookEntity.title
            subtitle = bookEntity.subtitle
        }
        return book
    }

    fun fromBookEntityListToBookList(listEntity: List<BookEntity>): List<Book> {
        val books = ArrayList<Book>(listEntity.size)
        listEntity.forEach { books.add(fromBookEntityToBook(it)) }
        return books
    }

    private fun fromBookToBookEntity(book: Book): BookEntity {
        val bookEntity = BookEntity()

        bookEntity.run {
            isbn13 = book.isbn13
            image = bookEntity.image
            url = book.url
            price = book.price
            title = book.title
            subtitle = book.subtitle
        }
        return bookEntity
    }

    fun fromBookEntityToDetails(details: BookEntity): BookNewJson {
        val bookEntity = BookNewJson()
        bookEntity.run {
            error = details.error
            language = details.language
            title = details.title
            subtitle = details.subtitle
            authors = details.authors
            publisher = details.publisher
            isbn13 = details.isbn13
            isbn10 = details.isbn10
            pages = details.pages
            year = details.year
            rating = details.rating
            desc = details.desc
            price = details.price
            image = details.image
            url = details.url
        }
        return bookEntity
    }

    fun fromDetailsToBookEntity(details: BookNewJson): BookEntity {
        val bookEntity = BookEntity()
        bookEntity.run {
            error = details.error
            language = details.language
            title = details.title
            subtitle = details.subtitle
            authors = details.authors
            publisher = details.publisher
            isbn13 = details.isbn13
            isbn10 = details.isbn10
            pages = details.pages
            year = details.year
            rating = details.rating
            desc = details.desc
            price = details.price
            image = details.image
            url = details.url
        }
        return bookEntity
    }

    fun fromBookListToBookEntityList(listEntity: List<Book>): List<BookEntity> {
        val entities = ArrayList<BookEntity>(listEntity.size)
        listEntity.forEach { entities.add(fromBookToBookEntity(it)) }
        return entities
    }

    private fun getBytesFromImageMethod(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        return image
    }

    private fun loadImageFromBytes(imageData: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
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