package com.example.uakpicomsysio8101.ui.photo

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import com.example.uakpicomsysio8101.R


class PhotoFragment : Fragment() {

    private lateinit var list: RecyclerView
    private val pictures = ArrayList<ImageView>()
    private lateinit var uploadButton: Button
    private lateinit var image: ImageView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        list = requireView().findViewById(R.id.pictures)
        val spannedGridLayoutManager = SpannedGridLayoutManager(
                SpannedGridLayoutManager.Orientation.VERTICAL,
                4
        )
        spannedGridLayoutManager.itemOrderIsStable = true

        list.layoutManager = spannedGridLayoutManager

        uploadButton = requireView().findViewById(R.id.upload_picture)
        uploadButton.setOnClickListener {
            val client = OkHttpClient()
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val gistJsonAdapter = moshi.adapter(PicturesGist::class.java)
            val request = Request.Builder()
                .url("https://pixabay.com/api/?key=19193969-87191e5db266905fe8936d565&q=hot+summer&image_type=photo&per_page=24")
                .build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                /*for ((name, value) in response.headers) {
                    println("$name: $value")
                }*/


                val gistPicture = gistJsonAdapter.fromJson(response.body!!.source())
                println(gistPicture)
                val arrOfImages = Array(gistPicture!!.hits.size) { ImageView(requireContext()) }
                for (i in gistPicture.hits.indices) {
                    //Glide.with(arrOfImages[i]).load(gistPicture.hits[i].previewURL).into(arrOfImages[i])

                    arrOfImages[i].setImageDrawable(drawableFromUrl(gistPicture.hits[i].previewURL))
                }
                println(arrOfImages[0].height)
                var adapter = MyPhotoAdapter(requireContext(), arrOfImages.toList())

                spannedGridLayoutManager.spanSizeLookup =
                    SpannedGridLayoutManager.SpanSizeLookup { position ->
                        if (position % 8 == 1 ) {
                            SpanSize(3, 3)
                        } else {
                            SpanSize(1, 1)
                        }
                    }
                list.adapter = adapter
            }
        }
    }
    @Throws(IOException::class)
    fun drawableFromUrl(url: String?): Drawable {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.getInputStream()
        x = BitmapFactory.decodeStream(input)
        return BitmapDrawable(Resources.getSystem(), x)
    }
}