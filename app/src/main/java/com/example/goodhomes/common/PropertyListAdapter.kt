package com.example.goodhomes.common

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.goodhomes.R
import com.example.goodhomes.customer.CustomerHomeFragment
import com.example.goodhomes.customer.PropertyFragment
import com.example.goodhomes.landlord.LandlordHomeFragment
import com.example.goodhomes.landlord.PropertyDetailsFragment
import com.google.android.material.card.MaterialCardView

class PropertyListAdapter(private var dataSet: List<Property>, private val activeFragment: Fragment):
    RecyclerView.Adapter<PropertyListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        // Initialize the view items
        val propertyImg: ImageView
        val txtLocation : TextView
        val txtName: TextView
        val txtDescription: TextView
        val txtPrice: TextView
        val propertyCard: MaterialCardView

        init {
            propertyImg = view.findViewById(R.id.propertyImg)
            txtLocation = view.findViewById(R.id.locationLbl)
            txtName = view.findViewById(R.id.nameLbl)
            txtDescription = view.findViewById(R.id.descLbl)
            txtPrice = view.findViewById(R.id.priceLbl)
            propertyCard = view.findViewById(R.id.propertyCard)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.property_item_recycler_view, parent, false);
        return ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind view items to data
        val property = dataSet[position]
        // Use Glide library to insert image into ImageView
//        Glide.with(holder.itemView.context)
//            .load(property.pictures)
//            .placeholder(R.drawable.ic_default_img)
//            .error(R.drawable.ic_default_img)
//            .into(holder.propertyImg)
        holder.txtLocation.text = property.location
        holder.txtName.text = property.name
        holder.txtDescription.text = property.description
        holder.txtPrice.text = "£${property.price}"
        // If active fragment is landlord's home or customer's home then assign click listener to card
        if (activeFragment is LandlordHomeFragment || activeFragment is CustomerHomeFragment) {
            holder.propertyCard.setOnClickListener { v ->
                // Create SharedPreferences for the property
                val sharedPreferences = v.context.getSharedPreferences("property", AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                // Put propertyId in SharedPreferences
                editor.putInt("propertyId", property.id)
                editor.apply()
                // If fragment is landlord home then replace current fragment with property details for landlord
                if (activeFragment is LandlordHomeFragment) {
                    (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PropertyDetailsFragment()).commit()
                }
                // If fragment is customer home then replace current fragment with property details for customer
                if (activeFragment is CustomerHomeFragment) {
                    (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, PropertyFragment()).commit()
                }
            }
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateData(newProperties: List<Property>) {
        dataSet = newProperties
        notifyDataSetChanged()
    }
}