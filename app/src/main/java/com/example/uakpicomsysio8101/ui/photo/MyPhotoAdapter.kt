package com.example.uakpicomsysio8101.ui.photo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.uakpicomsysio8101.R


class MyPhotoAdapter(
        private val context: Context,
        private var values: List<ImageView>
) : RecyclerView.Adapter<MyPhotoAdapter.MyPictureViewHolder>() {

    private lateinit var list: RecyclerView

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: MyPictureViewHolder, position: Int) {
        holder.bind(values[position], context)
    }

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPictureViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_photo, parent, false)
        return MyPictureViewHolder(view)
    }

    inner class MyPictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.image)

        fun bind(value: ImageView, context: Context) {
            image.setImageDrawable(value.drawable)
        }
    }
}