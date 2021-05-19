package ir.ahoora.musicpro.data

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ir.ahoora.musicpro.data.model.Duration
import ir.ahoora.musicpro.data.repository.MusicsRepo
import kotlin.math.min

class MainViewModel(private val context: Context) : ViewModel() {
    val musicsUris = MutableLiveData<ArrayList<Uri>>()
    lateinit var mp: MediaPlayer

    /* if you click on an item in the recylerView, this variable will be true
    * if the user clicks twice on the item, two instances of PlayActivity will
    * open. this variable is used to prevent this event */
    val currMusicUri = MutableLiveData<Uri>()

    var musicIsPlaying = false

    init {
        musicsUris.value = MusicsRepo(context).getUris()
    }

    fun initMediaPlayer() {
        musicIsPlaying = true

        mp = MediaPlayer.create(context, currMusicUri.value)
        mp.start()
    }

    fun playPreviousMusic() {
        //getting the index of current playing music
        var index = musicsUris.value!!.indexOf(currMusicUri.value)

        if (index == 0)
            index = musicsUris.value!!.size - 1
        else
            index--

        stopPlayingMusic()
        currMusicUri.value = musicsUris.value!![index]
    }

    fun playNextMusic() {
        //getting the index of current playing music
        var index = musicsUris.value!!.indexOf(currMusicUri.value)

        if (index == musicsUris.value!!.size - 1)
            index = 0
        else
            index++

        stopPlayingMusic()
        currMusicUri.value = musicsUris.value!![index]
    }

    fun getMusicImg(): Bitmap? {
        val array = MusicsRepo(context).getThumbnail(currMusicUri.value!!)

        if (array == null)
            return null

        return BitmapFactory.decodeByteArray(array, 0, array.size)
    }

    fun stopPlayingMusic() {
        try {
            mp.stop()
            mp.reset()
            mp.release()

        } catch (e: Exception) {

        }
    }

    fun miliSecondsToDuration(ms: Int): Duration {
        var seconds = ms / 1000
        val minutes = seconds / 60
        seconds -= minutes * 60

        return Duration(minutes, seconds)
    }
}