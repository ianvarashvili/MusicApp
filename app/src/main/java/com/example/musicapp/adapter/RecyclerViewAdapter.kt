package com.example.musicapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.databinding.ItemBinding
import com.example.musicapp.models.Song
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class RecyclerViewAdapter(
    private val itemList: List<Song>,
    private val onPlayClick: (Song) -> Unit,
//                          private val onStopClick: (Song) -> Unit

) : RecyclerView.Adapter<RecyclerViewAdapter.Holder>() {

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val item = itemList[position]
        val scaleAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.scale_up)

        holder.binding.apply {
            title.text = item.title
            artist.text = item.artist

            Glide.with(holder.binding.root).load(item.image).circleCrop().into(cover)
            // Fixed play icon (set in XML or here)
            playbtn.setImageResource(R.drawable.play)

            playbtn.setOnClickListener {
                // Apply the scale animation
                playbtn.startAnimation(scaleAnimation)
                onPlayClick(item)
            }
//            stopbtn.setOnClickListener {
//                onStopClick(item)
//            }
        }
    }

    override fun getItemCount() = itemList.size

}