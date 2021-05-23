package com.example.uakpicomsysio8101.ui.book

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uakpicomsysio8101.R
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


    private fun initBook(books: List<Book>) {
        val mapper = ObjectMapper()
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
                    val client = OkHttpClient()
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val gistJsonAdapter = moshi.adapter(BookAdapter::class.java)
                    val request = Request.Builder()
                            .url("https://api.itbook.store/1.0/search/${s.toString()}")
                            .build()

                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        /*for ((name, value) in response.headers) {
                            println("$name: $value")
                        }*/

                        //println(response.body!!.string())
                        val gist = gistJsonAdapter.fromJson(response.body!!.source())
                        initBook(gist!!.books)
                        println(gist)


                    }
                }
            }
        })
    }

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