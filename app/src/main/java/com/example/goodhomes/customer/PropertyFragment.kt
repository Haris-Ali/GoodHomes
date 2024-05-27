package com.example.goodhomes.customer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import org.json.JSONException
import org.json.JSONObject

class PropertyFragment : Fragment() {
    private var propertyId: Int? = -1
    private var userId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_property, container, false)

        val btnEnquire = view.findViewById<Button>(R.id.btnEnquiry) // Variable to hold view of enquiry button

        // Get property id from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("property", AppCompatActivity.MODE_PRIVATE)
        propertyId = sharedPreferences?.getInt("propertyId", -1)

        // Get user id from SharedPreferences
        val sharedPreferencesUser = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        userId = sharedPreferencesUser?.getInt("userId", -1)

        if (propertyId != -1) {
            val queue = Volley.newRequestQueue(activity) // Create Volley request queue
            val url = Urls.propertyDetailsForCustomerUrl + "?id=${propertyId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val propertyDetails = obj.getJSONObject("propertyDetails") // Get property details as JSON object from backend

                        // Variables to hold view items
                        val txtPropertyName = view.findViewById<TextView>(R.id.propertyNameLbl)
                        val txtPropertyLocation = view.findViewById<TextView>(R.id.propertyLocationLbl)
                        val txtPropertyDescription = view.findViewById<TextView>(R.id.propertyDescriptionLbl)
                        val txtPropertyPrice = view.findViewById<TextView>(R.id.propertyPriceLbl)

                        val txtLandlordName = view.findViewById<TextView>(R.id.landlordNameLbl)
                        val txtLandlordEmail = view.findViewById<TextView>(R.id.landlordEmailLbl)

                        // Assign property data to appropriate view items
                        txtPropertyName.text = propertyDetails.getString("property_name")
                        txtPropertyLocation.text = propertyDetails.getString("property_location")
                        txtPropertyDescription.text = propertyDetails.getString("property_description")
                        txtPropertyPrice.text = propertyDetails.getString("property_price")
                        txtLandlordName.text = propertyDetails.getString("landlord_fullName")
                        txtLandlordEmail.text = propertyDetails.getString("landlord_email")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle error message
                    val responseBody = error.networkResponse?.data?.let { String(it, Charsets.UTF_8) }
                    var errorMessage: String = "Unknown error occurred"
                    if (error.message != null) {
                        errorMessage = error.message.toString()
                    } else {
                        try {
                            val errorObj = JSONObject(responseBody.toString())
                            errorMessage = errorObj.getString("message")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    toastMessage(activity, errorMessage)
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return HashMap()
                }
            }
            queue.add(stringRequest)
        } else {
            // Redirect to home fragment
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomerHomeFragment()).commit()
        }

        btnEnquire.setOnClickListener { enquire() }

        return view
    }

    private fun enquire() {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager // Get the fragment manager
        val enquiryDialogFragment = EnquiryDialogFragment(propertyId, userId) // Variable for enquiry dialog fragment
        enquiryDialogFragment.show(fragmentManager, "enquiry_dialog") // Show this dialog in the current fragment
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}