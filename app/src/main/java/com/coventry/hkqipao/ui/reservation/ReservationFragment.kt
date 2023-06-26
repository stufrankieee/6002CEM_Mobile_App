package com.coventry.hkqipao.ui.reservation

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.coventry.hkqipao.MainActivity
import com.coventry.hkqipao.R
import com.coventry.hkqipao.databinding.FragmentReservationBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ReservationFragment : Fragment() {

    companion object {
        private const val TAG = "ReservationFragment"
    }

    private var _binding: FragmentReservationBinding? = null

    private lateinit var navView: BottomNavigationView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        navView = mainActivity.getBottomNavigationView()

        val rootView: View = requireView()

        val database = FirebaseDatabase.getInstance()
        val reservationRef = database.getReference("reservation")

        val user = Firebase.auth.currentUser

        val buttonSubmitRecord: Button = binding.buttonSubmitReservation

        buttonSubmitRecord.setOnClickListener {

            // Hide keyboard when clicked the submit button
            val imm = requireActivity().getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(rootView.windowToken, 0)

            val customerName = binding.edittextCustomerName.text.toString().trim()
            val emailAddress = binding.edittextEmailAddress.text.toString().trim()
            val phoneNumber = binding.edittextPhoneNumber.text.toString().trim()
            val dateOfRental = binding.edittextDateOfRental.text.toString().trim()
            val numberOfPeople = binding.edittextNumberOfPeople.text.toString().trim()
            val remark = binding.edittextRemark.text.toString().trim()
            if (areStringsNullOrEmpty(
                    customerName,
                    emailAddress,
                    phoneNumber,
                    dateOfRental,
                    numberOfPeople
                )
            ) {
                showSnackbar(rootView, "One or more fields are empty")
            } else {
                // Create an instance of your reservation entry using the input values
                val reservationEntry = ReservationEntry(
                    customerName,
                    emailAddress,
                    phoneNumber,
                    dateOfRental,
                    numberOfPeople,
                    remark
                )

                user?.let {
                    reservationRef.child(it.uid).setValue(reservationEntry)
                        .addOnSuccessListener {
                            // Record submitted successfully
                            // Handle any further actions or UI updates here
                            Log.d(TAG, "Success")
                            Snackbar.make(
                                rootView,
                                getString(R.string.reservation_status_reservation_submitted),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { error ->
                            // An error occurred while submitting the record
                            // Handle the error appropriately
                            Log.d(TAG, "Fail: $error")
                            Snackbar.make(
                                rootView,
                                getString(R.string.reservation_status_reservation_failed),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Success
                            } else {
                                // Failure
                                val exception = task.exception
                                // Log or handle the exception as needed
                                Log.d(TAG, "Exception: $exception")
                            }
                        }
                }
            }
        }
    }

    private fun areStringsNullOrEmpty(vararg strings: String?): Boolean {
        return strings.any { it.isNullOrEmpty() }
    }

    private fun showSnackbar(view: View, message: String) {
        val duration = Snackbar.LENGTH_SHORT
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.setAction("Action") {
            // Handle action button click here
        }
        snackbar.setAnchorView(navView)?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}