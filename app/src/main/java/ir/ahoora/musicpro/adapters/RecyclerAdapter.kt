package ir.ahoora.musicpro.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ir.ahoora.musicpro.R
import ir.ahoora.musicpro.data.repository.MusicsRepo
import ir.ahoora.musicpro.databinding.RecyclerViewholderBinding
import ir.ahoora.musicpro.util.MainRecyclerViewInterface

class RecyclerAdapter(
    var list: ArrayList<Uri>,
    var mainRecyclerViewInterface: MainRecyclerViewInterface
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    val allMusics = list
    private lateinit var context: Context

    inner class ViewHolder(var binding: RecyclerViewholderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            val uri = list[position]

            binding.musicName.text = MusicsRepo(context).getNameByUri(uri)

            val duration = MusicsRepo(context).getDurationByUri(uri)
            binding.durationTv.text = "${duration.minute}:${duration.seconds}"

            val thumbnail = MusicsRepo(context).getThumbnail(uri)
            if (thumbnail != null)
                binding.musicImg.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        thumbnail,
                        0,
                        thumbnail.size
                    )
                )
            else
                binding.musicImg.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.music
                    )
                )

            binding.root.setOnClickListener {
                mainRecyclerViewInterface.onItemClicked(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewholderBinding.inflate(LayoutInflater.from(parent.context))

        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //get the context
        context = holder.itemView.context

        holder.bind(position)
    }

    override fun getItemCount(): Int = list.size
}