package ir.ahoora.musicpro.ui

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ir.ahoora.musicpro.PlayActivity
import ir.ahoora.musicpro.R
import ir.ahoora.musicpro.adapters.RecyclerAdapter
import ir.ahoora.musicpro.data.MainViewModel
import ir.ahoora.musicpro.data.MainViewModelFactory
import ir.ahoora.musicpro.databinding.ActivityMainBinding
import ir.ahoora.musicpro.ui.custom.AppcompatActivity
import ir.ahoora.musicpro.util.MainRecyclerViewInterface

class MainActivity : AppcompatActivity(
    arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
), MainRecyclerViewInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        initRecyclerView()
        recyclerAdapter = RecyclerAdapter(viewModel.musicsUris.value!!, this)
        binding.recycler.adapter = recyclerAdapter

        //listeners and observers
        viewModel.musicsUris.observe(this, musicsUrisChanged)
    }

    private fun initRecyclerView() {
        binding.recycler.layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.VERTICAL, false
        )
    }

    private val musicsUrisChanged = Observer<ArrayList<Uri>> {
        binding.recycler.adapter?.notifyDataSetChanged()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(this)
        )[MainViewModel::class.java]
    }

    /* recyclerView item is clicked so play the music */
    override fun onItemClicked(position: Int) {
        if (!viewModel.musicIsPlaying) {
            val intent = Intent(this, PlayActivity::class.java)
            intent.data = viewModel.musicsUris.value!![position]
            startActivity(
                intent, ActivityOptions.makeCustomAnimation(
                    this,
                    R.anim.playactivity_enter_anim, R.anim.empty_anim
                ).toBundle()
            )
        }
    }
}