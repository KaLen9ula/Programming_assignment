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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import com.example.uakpicomsysio8101.R


class PhotoFragment : Fragment() {

    private lateinit var list: RecyclerView
    private val pictures = ArrayList<ImageView>()
    private lateinit var uploadButton: Button
    private lateinit var image: ImageView
    private lateinit var dao: PhotoDAO
    private lateinit var photoDbHelper: PhotoDBHelper

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
        photoDbHelper = PhotoDBHelper(requireActivity())
        dao = PhotoDBHelper.instance.getImageTestDao()

        uploadButton.setOnClickListener {
            if (runBlocking { dao.getAll().isEmpty() })
                try {
                    downloadData(spannedGridLayoutManager)
                } catch (e: Exception) {
                    println(e.message)
                    Toast.makeText(
                        requireContext(),
                        "No internet connection and database is empty!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            else {
                val images = PhotoDBHelper.getImages()
                val imageViews = ArrayList<ImageView>(images.size)
                repeat(images.size) { imageViews.add(ImageView(requireContext())) }
                for (i in imageViews.indices) {
                    imageViews[i].setImageBitmap(images[i])
                }

                val adapter = MyPhotoAdapter(requireContext(), imageViews)

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

    private fun downloadData(spannedGridLayoutManager: SpannedGridLayoutManager) {
        val client = OkHttpClient()
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val gistJsonAdapter = moshi.adapter(PicturesGist::class.java)


        val request = Request.Builder()
            .url("https://pixabay.com/api/?key=19193969-87191e5db266905fe8936d565&q=night+city&image_type=photo&per_page=27")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val gistPicture = gistJsonAdapter.fromJson(response.body!!.source())
            println(gistPicture)
            val arrOfImages = Array(gistPicture!!.hits.size) { ImageView(requireContext()) }
            for (i in gistPicture.hits.indices) {
                //Glide.with(arrOfImages[i]).load(gistPicture.hits[i].previewURL).into(arrOfImages[i])

                arrOfImages[i].setImageDrawable(drawableFromUrl(gistPicture.hits[i].previewURL))
            }

            val adapter = MyPhotoAdapter(requireContext(), arrOfImages.toList())
            val imageEntityList = ArrayList<Bitmap>(arrOfImages.size)
            for (i in arrOfImages.indices) {
                arrOfImages[i].invalidate()
                val drawable = arrOfImages[i].drawable as BitmapDrawable
                imageEntityList.add(drawable.bitmap)

            }
            println(PhotoDBHelper.setImages(imageEntityList.toList()))
            println(runBlocking { PhotoDBHelper.getImages() })
            println("gg")

            spannedGridLayoutManager.spanSizeLookup =
                SpannedGridLayoutManager.SpanSizeLookup { position ->
                    if (position % 9 == 4) {
                        SpanSize(2, 2)
                    } else {
                        SpanSize(1, 1)
                    }
                }
            list.adapter = adapter
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

    private fun getBytesFromImageMethod(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val image = stream.toByteArray()
        return image
    }
}