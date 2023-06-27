package com.coventry.hkqipao.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coventry.hkqipao.R

class ReservationRecordAdapter(private val dataSet: MutableList<ReservationRecordsEntry>) :
    RecyclerView.Adapter<ReservationRecordAdapter.ViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(reservation: ReservationRecordsEntry)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDateOfRental: TextView = view.findViewById(R.id.text_date_of_rental)
        val textReservationInfo: TextView = view.findViewById(R.id.text_reservation_info)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener?.onItemClick(dataSet[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation_records, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = dataSet[position]
        holder.textDateOfRental.text = reservation.dateOfRental
        holder.textReservationInfo.text = reservation.customerName
    }

    override fun getItemCount() = dataSet.size
}