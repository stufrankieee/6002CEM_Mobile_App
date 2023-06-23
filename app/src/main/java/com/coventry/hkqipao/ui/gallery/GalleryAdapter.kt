package com.coventry.hkqipao.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.coventry.hkqipao.R
import android.widget.ProgressBar
import com.coventry.hkqipao.model.Photo

class GalleryAdapter(
    private val photos: List<Photo>,
    private val isLoading: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_PHOTO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val loadingView = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false)
                LoadingViewHolder(loadingView)
            }
            VIEW_TYPE_PHOTO -> {
                val photoView = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false)
                PhotoViewHolder(photoView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> {
                // Bind data and handle loading view
            }
            is PhotoViewHolder -> {
                val photo = photos[position]

                Glide.with(holder.itemView)
                    .load(photo.download_url)
//                    .thumbnail(0.25f) // Load a low-resolution thumbnail first
//                    .placeholder(R.drawable.ic_placeholder) // Optional placeholder image while loading
//                    .error(R.drawable.ic_error_image) // Optional error image if the load fails
                    .into(holder.imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        // Add 1 to the count for the loading indicator
        return photos.size + if (isLoading) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        // Return a different view type for the loading indicator
        return if (position < photos.size) VIEW_TYPE_PHOTO else VIEW_TYPE_LOADING
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}