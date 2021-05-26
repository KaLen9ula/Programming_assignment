package com.example.uakpicomsysio8101.ui.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference


class PhotoDBHelper(context: Context) {

    init {
        PhotoDBHelper.context = WeakReference(context)
        createDatabase()
    }

    companion object {

        private var context: WeakReference<Context>? = null
        private const val DATABASE_NAME: String = "image"
        private var singleton: PictureDB? = null

        private fun createDatabase(): PictureDB {
            return Room.databaseBuilder(
                context?.get()
                    ?: throw IllegalStateException("initialize by calling constructor before calling DBHelper.instance"),
                PictureDB::class.java,
                DATABASE_NAME
            )
                .build()
        }


        val instance: PictureDB
            @Synchronized get() {
                if (null == singleton)
                    singleton = createDatabase()

                return singleton as PictureDB
            }

        fun setImage(img: Bitmap) = runBlocking {
            val dao = instance.getImageTestDao()
            val imageTest = PhotoEntity()
            imageTest.image = getBytesFromImageMethod(img)
            dao.upsertByReplacement(listOf(imageTest))
        }

        fun setImages(images: List<Bitmap>) {
            images.forEach { x -> setImage(x) }
        }

        fun getImages(): List<Bitmap> = runBlocking {
            val dao = instance.getImageTestDao()
            val imageByteArray = dao.getAll()
            loadImageFromBytes(imageByteArray)
        }

        private fun getBytesFromImageMethod(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            return image
        }

        private fun loadImageFromBytes(imageData: List<PhotoEntity>): List<Bitmap> {
            val bitmaps = ArrayList<Bitmap>()
            imageData.forEach { x -> bitmaps.add(BitmapFactory.decodeByteArray(x.image, 0, x.image!!.size))}
            return bitmaps
        }
    }
}