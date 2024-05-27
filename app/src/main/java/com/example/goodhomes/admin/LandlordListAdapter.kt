package com.example.goodhomes.admin

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.goodhomes.R
import com.google.android.material.card.MaterialCardView


class LandlordListAdapter(private var dataSet: List<Landlord>) :
    RecyclerView.Adapter<LandlordListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        // Initialize the view's item for this recycler view
        val txtUsername : TextView
        val txtEmail: TextView
        val txtNumOfProperties: TextView
        val cardView: MaterialCardView

        init {
            txtUsername = view.findViewById(R.id.txtUsername)
            txtEmail = view.findViewById(R.id.txtEmail)
            txtNumOfProperties = view.findViewById(R.id.txtNumOfProperties)
            cardView = view.findViewById(R.id.landlord_item_card)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.landlord_item_layout, parent, false);
        return ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Binding each view item
        val landlord = dataSet[position]
        holder.txtUsername.text = landlord.fullName
        holder.txtEmail.text = landlord.email
        holder.txtNumOfProperties.text = "${holder.itemView.context.getString(R.string.number_properties_label)}: ${landlord.property_count}"
        // Setting on click listener for the card to navigate to landlord's details fragment
        holder.cardView.setOnClickListener { v ->
            // Create new SharedPreferences for the landlord
            val sharedPreferences = v.context.getSharedPreferences("landlord", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            // Put landlord's id, name and email in the SharedPreferences
            editor.putInt("landlordId", landlord.id)
            editor.putString("landlordName", landlord.fullName)
            editor.putString("landlordEmail", landlord.email)
            editor.apply()
            // Replace current fragment with landlord details fragment
            (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LandlordProfileAdminFragment()).commit()
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateData(newLandlords: List<Landlord>) {
        dataSet = newLandlords
        notifyDataSetChanged()
    }
}