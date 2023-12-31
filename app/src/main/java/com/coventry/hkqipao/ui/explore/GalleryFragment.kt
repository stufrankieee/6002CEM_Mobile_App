package com.coventry.hkqipao.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coventry.hkqipao.databinding.FragmentExploreBinding
import com.coventry.hkqipao.databinding.FragmentGalleryBinding
import com.coventry.hkqipao.model.Photo
import com.coventry.hkqipao.network.ApiServiceInstance
import com.coventry.hkqipao.ui.gallery.GalleryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Display photos for user insight
    private lateinit var galleryAdapter: GalleryAdapter
    private val photos: MutableList<Photo> = mutableListOf()
    private var currentVisibleItemPosition = 0
    private var currentVisibleItemOffset = 0
    private var isLoading = false
    private var page = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerViewExplore: RecyclerView = binding.recyclerViewExplore
        recyclerViewExplore.layoutManager = GridLayoutManager(requireContext(), 2)

        // Set up RecyclerView with your custom adapter
        galleryAdapter = GalleryAdapter(emptyList(), isLoading)
        recyclerViewExplore.adapter = galleryAdapter

        // Fetch the initial photo list from the API
        fetchPhotoList()

        // Add scroll listener for pagination
        recyclerViewExplore.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!isLoading && lastVisibleItemPosition == photos.size - 1) {
                        // Load more data
                        page++
                        fetchPhotoList()
                    }
                }
            }
        })

        return root
    }

    private fun fetchPhotoList() {
        // Update the flag before making the API call
        isLoading = true

        // Store the current visible item position
        val layoutManager = binding.recyclerViewExplore.layoutManager as LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleItemView = binding.recyclerViewExplore.getChildAt(0)
        val offset = firstVisibleItemView?.top ?: 0

        // Make Retrofit API call to fetch the photo list for the given page
//        Toast.makeText(requireContext(), "Page No: $page", Toast.LENGTH_SHORT).show()

        ApiServiceInstance.apiService.getPhotoList(page, 15).enqueue(object : Callback<List<Photo>> {
            override fun onResponse(call: Call<List<Photo>>, response: Response<List<Photo>>) {
                isLoading = false
                if (response.isSuccessful) {
                    val photoList = response.body()
                    if (photoList != null) {
                        // Update the RecyclerView adapter with new data
                        photos.addAll(photoList)
                        galleryAdapter = GalleryAdapter(photos, isLoading)
                        binding.recyclerViewExplore.adapter = galleryAdapter
                        galleryAdapter.notifyDataSetChanged()

                        // Restore the scroll position
                        (binding.recyclerViewExplore.layoutManager as LinearLayoutManager)?.scrollToPositionWithOffset(firstVisibleItemPosition, offset)
                    }
                } else {
                    // Handle API error
                }
            }

            override fun onFailure(call: Call<List<Photo>>, t: Throwable) {
                isLoading = false
                // Handle network error
            }
        })
    }

//    private fun showLightbox(photo: Photo) {
//        val dialog = Dialog(requireContext(), R.style.Theme_Black_NoTitleBar_Fullscreen)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_lightbox)
//
//        val photoImageView: ImageView = dialog.findViewById(R.id.photoImageView)
//
//        Glide.with(dialog.context)
//            .load(photo.downloadUrl)
//            .into(photoImageView)
//
//        dialog.show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}