package com.coventry.hkqipao.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.coventry.hkqipao.databinding.ActivityReservationRecordsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ReservationRecordActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ReservationRecordActivity"
    }

    private lateinit var viewBinding: ActivityReservationRecordsBinding
    private lateinit var navView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityReservationRecordsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val rootView: View = viewBinding.root

        val database = FirebaseDatabase.getInstance()
        val reservationRef = database.getReference("reservation")
        val listReservationRecord: RecyclerView = viewBinding.listReservationRecord

        val user = Firebase.auth.currentUser
        val selectedItem = intent?.getStringExtra("selectedItem")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = selectedItem

        val userId = user?.uid
        val reservationsRef = FirebaseDatabase.getInstance().getReference("reservation/$userId")

        reservationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reservationRecordEntries = mutableListOf<ReservationRecordsEntry>()

                for (dateSnapshot in snapshot.children) {
                    val date = dateSnapshot.key
                    val reservationData = dateSnapshot.getValue(ReservationData::class.java)

                    if (reservationData != null) {
                        // Create a Reservation object and add it to the list
                        val reservationRecordsEntry = date?.let { ReservationRecordsEntry(it, reservationData.customerName, reservationData.dateOfRental, reservationData.emailAddress, reservationData.numberOfPeople, reservationData.phoneNumber, reservationData.remark) }
                        if (reservationRecordsEntry != null) {
                            reservationRecordEntries.add(reservationRecordsEntry)
                        }
                    }
                }

                // Step 5: Create an Adapter for the RecyclerView
                val reservationRecordAdapter = ReservationRecordAdapter(reservationRecordEntries)

                // Set the adapter on the RecyclerView
                listReservationRecord.adapter = reservationRecordAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })

        Log.d(TAG, "Screen: ${selectedItem}")
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
}