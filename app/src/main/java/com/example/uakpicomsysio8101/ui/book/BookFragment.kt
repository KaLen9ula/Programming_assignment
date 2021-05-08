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

        initBook()
        initSearchField()
        val arr = bookContainer.search
        adapter = ViewAdapterRecycler(requireContext(), arr) { item ->
            if (item.isbn13 != null && item.isbn13!!.isNotEmpty()) {
                val id = getResId(item.isbn13!!, R.string::class.java)
                val jsonText = requireContext().resources.getString(id)
                val intent = Intent(requireContext(), BookDetailsActivity::class.java)
                intent.putExtra("info", jsonText)
                startActivity(intent)
            }
        }
        view.findViewById<RecyclerView>(R.id.list).adapter = adapter
        enableSwipeToDeleteAndUndo()
        addButton = requireView().findViewById(R.id.add)
        addButton.setOnClickListener { withEditText(addButton)
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

    private fun initBook() {
        val mapper = ObjectMapper()
        val jsonText = requireView().resources.getString(R.string.json_books)
        bookContainer = mapper.readValue(jsonText, BookContainer::class.java)
    }
    private fun initSearchField() {


        searchField = requireView().findViewById(R.id.filter)
        searchField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
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
        val swipeToDelete: SwipeToDelete = object : SwipeToDelete(
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
        val itemTouchhelper = ItemTouchHelper(swipeToDelete)
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