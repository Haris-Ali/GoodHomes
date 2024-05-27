package com.example.goodhomes.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goodhomes.R

class MyEnquiriesListAdapter(private var dataSet: List<MyEnquiry>):
    RecyclerView.Adapter<MyEnquiriesListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        // Initialize the view items
        val txtEnquiryText: TextView
        val txtPropertyName: TextView
        val txtPropertyLocation: TextView
        val txtLandlordName: TextView
        val txtLandlordEmail: TextView

        init {
            txtEnquiryText = view.findViewById(R.id.txtEnquiryLbl)
            txtPropertyName = view.findViewById(R.id.txtPropertyName)
            txtPropertyLocation = view.findViewById(R.id.txtPropertyLocation)
            txtLandlordName = view.findViewById(R.id.txtLandlordName)
            txtLandlordEmail = view.findViewById(R.id.txtLandlordEmail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.customer_enquiry_item_recycler_view, parent, false);
        return ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind view items to data
        val enquiry = dataSet[position]
        holder.txtEnquiryText.text = holder.itemView.context.getString(R.string.enquiry_Lbl)
        holder.txtPropertyName.text = "${holder.itemView.context.getString(R.string.userEnquiry_propertyName_label)} ${enquiry.property_name}"
        holder.txtPropertyLocation.text = "${holder.itemView.context.getString(R.string.enquiry_location_lbl)} ${enquiry.property_location}"
        holder.txtLandlordName.text = "${holder.itemView.context.getString(R.string.enquiry_name_lbl)} ${enquiry.landlord_fullName}"
        holder.txtLandlordEmail.text = "${holder.itemView.context.getString(R.string.enquiry_email_lbl)} ${enquiry.landlord_email}"
    }

    override fun getItemCount() = dataSet.size
}