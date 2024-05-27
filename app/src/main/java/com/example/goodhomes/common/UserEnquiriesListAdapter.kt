package com.example.goodhomes.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.goodhomes.R

class UserEnquiriesListAdapter(private var dataSet: List<Enquiry>):
    RecyclerView.Adapter<UserEnquiriesListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        // Initialize the view items
        val txtName : TextView
        val txtPropertyName: TextView
        val txtContactDetails: TextView
        val txtMessage: TextView

        init {
            txtName = view.findViewById(R.id.txtName)
            txtPropertyName = view.findViewById(R.id.txtPropertyName)
            txtContactDetails = view.findViewById(R.id.txtContactDetails)
            txtMessage = view.findViewById(R.id.txtMessage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.landlord_enquiry_item_recycler_view, parent, false);
        return ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind view items to data
        val enquiry = dataSet[position]
        holder.txtName.text = "${enquiry.user_fullName} ${holder.itemView.context.getString(R.string.userEnquiry_text)}"
        holder.txtPropertyName.text = "${holder.itemView.context.getString(R.string.userEnquiry_propertyName_label)} ${enquiry.property_name}"
        holder.txtContactDetails.text = "${holder.itemView.context.getString(R.string.userEnquiry_contactDetails_label)} ${enquiry.user_email}"
        holder.txtMessage.text = "${holder.itemView.context.getString(R.string.userEnquiry_msg)} ${enquiry.message}"
    }

    override fun getItemCount() = dataSet.size
}