package com.coventry.hkqipao.ui.explore

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coventry.hkqipao.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_place_details, container, false)

        val backgroundView = view.findViewById<View>(R.id.backgroundView)
        val placeNameTextView = view.findViewById<TextView>(R.id.placeNameTextView)
        val placeIntroductionTextView = view.findViewById<TextView>(R.id.placeIntroductionTextView)
        backgroundView.setOnTouchListener { _, event -> true }

        // Retrieve the place name and introduction from arguments
        val placeName = arguments?.getString("placeName")
        val placeIntroduction = arguments?.getString("placeIntroduction")

        placeNameTextView.text = placeName
        placeIntroductionTextView.text = placeIntroduction

        // Set the behavior of the bottom sheet
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            bottomSheetDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val bottomSheet: View? = bottomSheetDialog.findViewById(R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)

            // Adjust the peek height to show 1/3 of the screen
            val windowHeight = Resources.getSystem().displayMetrics.heightPixels
            val peekHeight = windowHeight / 3
            behavior.peekHeight = peekHeight

            // Set the behavior of the bottom sheet
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Handle state changes of the bottom sheet if needed
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // Handle slide offset changes of the bottom sheet if needed
                }
            })

        }

        return view
    }
}
