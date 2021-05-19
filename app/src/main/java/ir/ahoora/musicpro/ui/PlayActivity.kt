package ir.ahoora.musicpro

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telecom.Call
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ir.ahoora.musicpro.data.MainViewModel
import ir.ahoora.musicpro.data.MainViewModelFactory
import ir.ahoora.musicpro.databinding.ActivityPlayBinding
import java.lang.Exception
import kotlin.math.min

class PlayActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityPlayBinding
    private lateinit var viewModel: MainViewModel
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        /* get the music from the intent and send it to the viewModel */
        viewModel.currMusicUri.value = intent.data

        handler.post(object: Runnable {
            override fun run() {
                try {
                    binding.seekbar.progress = viewModel.mp.currentPosition

                    refreshCurrpos()

                } catch (e: Exception) {
                    binding.seekbar.progress = 0
                }

                handler.postDelayed(this, 1000)
            }
        })

        /* observers and listeners */
        viewModel.currMusicUri.observe(this, currMusicUriChanged)
        binding.playBtn.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.prevBtn.setOnClickListener(this)
        binding.seekbar.setOnSeekBarChangeListener(this)
    }

    private val currMusicUriChanged = Observer<Uri> {
        loadMusicImg()
        viewModel.initMediaPlayer()
        loadMusicDuration()
        setUpSeekbar()
    }

    private fun refreshCurrpos() {
        val duration = viewModel.miliSecondsToDuration(viewModel.mp.currentPosition)

        val minutes = duration.minute.toString()
        val seconds = duration.seconds.toString()

        binding.currPosTv.text = "$minutes:$seconds"
    }

    private fun loadMusicDuration() {
        val duration = viewModel.miliSecondsToDuration(viewModel.mp.duration)

        val minutes = duration.minute.toString()
        val seconds = duration.seconds.toString()

        binding.durationTv.text = "$minutes:$seconds"
    }

    private fun loadMusicImg() {
        val img = viewModel.getMusicImg()

        if (img == null) {
            binding.musicImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.music))
            return
        }

        binding.musicImg.setImageBitmap(img)
    }

    private fun setUpSeekbar() {
        binding.seekbar.max = viewModel.mp.duration
        binding.seekbar.progress = viewModel.mp.currentPosition
    }

    private fun init() {
        viewModel  = ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]

        binding = ActivityPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.playBtn -> {
                binding.playBtn.icon = ContextCompat.getDrawable(this,
                    if (viewModel.mp.isPlaying) R.drawable.play else R.drawable.pause
                )

                if (viewModel.mp.isPlaying)
                    viewModel.mp.pause()
                else
                    viewModel.mp.start()
            }

            binding.prevBtn -> {
                viewModel.playPreviousMusic()
            }

            binding.nextBtn -> {
                viewModel.playNextMusic()
            }
        }
    }

    override fun finish() {
        super.finish()

        viewModel.stopPlayingMusic()
        overridePendingTransition(R.anim.empty_anim, R.anim.playactivity_exit_anim)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser)
            return
        viewModel.mp.seekTo(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}