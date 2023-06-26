package com.coventry.hkqipao.ui.explore

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.coventry.hkqipao.databinding.FragmentPlaceBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PlaceFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val DEFAULT_ZOOM_LEVEL = 15f
    }

    private var _binding: FragmentPlaceBinding? = null
    private lateinit var mapView: MapView


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val map = googleMap

        // Enable the user's location
        // Check if the location permission is granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Location permission is granted, enable the user's location
            map.isMyLocationEnabled = true
        } else {
            // Location permission is not granted, request the permission from the user
            // Handle the permission request response in the onRequestPermissionsResult method
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSION_REQUEST_CODE
            )
        }

        // Add a marker for the attractions
        // Initialize Firebase Realtime Database reference
        val database = FirebaseDatabase.getInstance()
        val placesRef = database.getReference("places")

        // Retrieve the place data from Firebase Realtime Database
        placesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Loop through each child node in the dataSnapshot
                for (placeSnapshot in dataSnapshot.children) {
                    // Retrieve the latitude and longitude coordinates for each place
                    val latitude = placeSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = placeSnapshot.child("longitude").getValue(Double::class.java)

                    // Add a marker on the map for each place
                    if (latitude != null && longitude != null) {
                        val attraction = LatLng(latitude, longitude)
                        val title =
                            placeSnapshot.key // Assuming the place name is stored as the key
                        map.addMarker(MarkerOptions().position(attraction).title(title))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occur during the data retrieval
            }
        })

        // Move the camera to the user's location
        val userLocation = LatLng(22.2827177, 114.1537154)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, DEFAULT_ZOOM_LEVEL))


        map.setOnMarkerClickListener { marker ->
            val placeName = marker.title

            // Retrieve the place details from the Realtime Database based on the placeName
            placesRef.child(placeName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val placeDetails = dataSnapshot.value as? Map<*, *>
                        val placeIntroductionFromDatabase = placeDetails?.get("introduction") as? String

                        // Create a new instance of the PlaceBottomSheetDialogFragment
                        val bottomSheetDialogFragment = PlaceBottomSheetDialogFragment()

                        // Pass the retrieved placeName and placeIntroduction as arguments
                        val args = Bundle()
                        args.putString("placeName", placeName)
                        args.putString("placeIntroduction", placeIntroductionFromDatabase)
                        bottomSheetDialogFragment.arguments = args

                        // Show the PlaceBottomSheetDialogFragment
                        bottomSheetDialogFragment.show(childFragmentManager, bottomSheetDialogFragment.tag)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })

            // Return true to indicate that the listener has consumed the event
            true
        }

    }
}