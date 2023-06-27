package com.coventry.hkqipao.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.coventry.hkqipao.R
import com.coventry.hkqipao.databinding.ActivityEditReservationBinding
import com.coventry.hkqipao.ui.reservation.ReservationEntry
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class EditReservationActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ReservationRecordActivity"
    }

    private lateinit var viewBinding: ActivityEditReservationBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityEditReservationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val rootView: View = viewBinding.root

        val database = FirebaseDatabase.getInstance()
        val reservationRef = database.getReference("reservation")

        val user = Firebase.auth.currentUser
        var selectedItemDate = intent?.getStringExtra("selectedItemDate")
        var selectedItemCustomerName= intent?.getStringExtra("selectedItemCustomerName")
        var selectedItemEmailAddress = intent?.getStringExtra("selectedItemEmailAddress")
        var selectedItemPhoneNumber = intent?.getStringExtra("selectedItemPhoneNumber")
        var selectedItemDateOfRental = intent?.getStringExtra("selectedItemDateOfRental")
        var selectedItemNumberOfPeople = intent?.getStringExtra("selectedItemNumberOfPeople")
        var selectedItemRemark = intent?.getStringExtra("selectedItemRemark")

        viewBinding.edittextCustomerName.setText(selectedItemCustomerName)
        viewBinding.edittextEmailAddress.setText(selectedItemEmailAddress)
        viewBinding.edittextPhoneNumber.setText(selectedItemPhoneNumber)
        viewBinding.edittextDateOfRental.setText(selectedItemDateOfRental)
        viewBinding.edittextNumberOfPeople.setText(selectedItemNumberOfPeople)
        viewBinding.edittextRemark.setText(selectedItemRemark)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.reservation_update_record) + " $selectedItemDate"

        val buttonUpdateRecord: Button = viewBinding.buttonUpdateReservation
        val buttonCancelRecord: Button = viewBinding.buttonCancelReservation

        buttonUpdateRecord.setOnClickListener {
            if (user == null) {
                showSnackbar(rootView, getString(R.string.reservation_status_sign_in_account))
            } else {
                // Hide keyboard when clicked the submit button
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)

                val customerName = viewBinding.edittextCustomerName.text.toString().trim()
                val emailAddress = viewBinding.edittextEmailAddress.text.toString().trim()
                val phoneNumber = viewBinding.edittextPhoneNumber.text.toString().trim()
                val dateOfRental = viewBinding.edittextDateOfRental.text.toString().trim()
                val numberOfPeople = viewBinding.edittextNumberOfPeople.text.toString().trim()
                val remark = viewBinding.edittextRemark.text.toString().trim()
                if (areStringsNullOrEmpty(
                        customerName,
                        emailAddress,
                        phoneNumber,
                        dateOfRental,
                        numberOfPeople
                    )
                ) {
                    showSnackbar(rootView, getString(R.string.reservation_status_required_field))
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
                        selectedItemDate?.let { it1 ->
                            reservationRef.child(it.uid).child(it1).setValue(reservationEntry)
                                .addOnSuccessListener {
                                    // Record submitted successfully
                                    // Handle any further actions or UI updates here
                                    Log.d(TAG, "Success")
                                    showSnackbar(rootView, getString(R.string.reservation_status_reservation_submitted))
                                    finish()
                                }
                                .addOnFailureListener { error ->
                                    // An error occurred while submitting the record
                                    // Handle the error appropriately
                                    Log.d(TAG, "Fail: $error")
                                    showSnackbar(rootView, getString(R.string.reservation_status_reservation_failed))

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
        }

        buttonCancelRecord.setOnClickListener {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun areStringsNullOrEmpty(vararg strings: String?): Boolean {
        return strings.any { it.isNullOrEmpty() }
    }

    private fun showSnackbar(view: View, message: String) {
        val duration = Snackbar.LENGTH_LONG
        val snackbar = Snackbar.make(view, message, duration)
        snackbar.show()
    }
}