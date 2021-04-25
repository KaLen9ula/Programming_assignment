package com.example.uakpicomsysio8101.ui.book

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uakpicomsysio8101.R
import com.fasterxml.jackson.databind.ObjectMapper


class BookFragment : Fragment() {
    private var columnCount = 1
    private var bookContainer: BookContainer = BookContainer()
    private lateinit var view: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        view = inflater.inflate(R.layout.fragment_book, container, false) as RecyclerView
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(view) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
        }

        initBook()
        val arr = bookContainer.search
        view.adapter = ViewAdapterRecycler(requireContext(), arr)
    }

    private fun initBook() {
        val mapper = ObjectMapper()
        val jsonText = requireView().resources.getString(R.string.json_book)
        bookContainer = mapper.readValue(jsonText, BookContainer::class.java)
    }
}