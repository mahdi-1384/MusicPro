package ir.ahoora.musicpro.data.repository

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import ir.ahoora.musicpro.data.model.Duration

class MusicsRepo(private val context: Context) {
    companion object {
        private val URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        private val NAME = MediaStore.Audio.AudioColumns.DISPLAY_NAME
        private val ID = MediaStore.Audio.AudioColumns._ID
        private val DATA = MediaStore.Audio.AudioColumns.DATA
        private val RELATIVE_PATH = MediaStore.Audio.AudioColumns.RELATIVE_PATH
        private val DURATION = MediaStore.Audio.AudioColumns.DURATION
    }

    private val resolver = context.contentResolver

    fun getUris(): ArrayList<Uri> {
        val result = ArrayList<Uri>()

        val cursor = resolver.query(
            URI,
            arrayOf(NAME, DURATION, DATA, ID),
            null, null,
            NAME + ""
        )!!

        while (cursor.moveToNext()) {
            val uri = Uri.withAppendedPath(URI, cursor.getString(cursor.getColumnIndex(ID)) + "")

            result.add(uri)
        }

        cursor.close()

        return result
    }

    fun getNameByUri(uri: Uri): String {
        var result = ""

        val cursor = resolver.query(
            uri,
            arrayOf(NAME),
            null, null,
            NAME + ""
        ) ?: return ""

        if (cursor.moveToNext())
            result = cursor.getString(cursor.getColumnIndex(NAME))

        cursor.close()

        return result
    }

    fun getDurationByUri(uri: Uri): Duration {
        var duration = ""

        val cursor = resolver.query(
            uri,
            arrayOf(DURATION, DATA),
            null, null,
            NAME + ""
        ) ?: return Duration(0,0)

        if (cursor.moveToNext()) {
            if (Build.VERSION.SDK_INT >= 28) {
                duration = cursor.getString(cursor.getColumnIndex(DURATION))
            } else {
                val m = MediaMetadataRetriever()
                m.setDataSource(cursor.getString(cursor.getColumnIndex(DATA)))
                duration = m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
            }
        }

        cursor.close()

        return milisecondToDuration(duration.toLong())
    }

    fun getThumbnail(uri: Uri): ByteArray? {
        try {
            val cursor = resolver.query(
                uri,
                null, null, null,
                NAME + ""
            ) ?: return ByteArray(0)

            var path = ""

            if (Build.VERSION.SDK_INT < 29) {
                if (cursor.moveToNext())
                    path = cursor.getString(cursor.getColumnIndex(DATA))
            } else {
                //api level > 29
                if (cursor.moveToNext())
                    path = cursor.getString(cursor.getColumnIndex(RELATIVE_PATH))
            }

            val m = MediaMetadataRetriever()
            m.setDataSource(path)
            return m.embeddedPicture

        } catch(e: Exception) {
            return null
        }
    }

    fun milisecondToDuration(ms: Long): Duration {
        var seconds = (ms / 1000).toInt()
        val minute = (seconds / 60)
        seconds -= minute * 60

        return Duration(minute, seconds)
    }
}