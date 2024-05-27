package com.example.goodhomes.landlord

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.example.goodhomes.common.Enquiry
import com.example.goodhomes.common.UserEnquiriesListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

class LandlordEnquiriesFragment : Fragment() {
    private lateinit var enquiriesAdapter: UserEnquiriesListAdapter
    private var landlordId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_landlord_enquiries, container, false)

        // Get landlord id from the SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        landlordId = sharedPreferences?.getInt("userId", -1)

        val rcView = view.findViewById<RecyclerView>(R.id.rcView) // Variable to hold recycler view item
        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) // Set layout manager for recycler view

        if (landlordId != -1) {
            val queue = Volley.newRequestQueue(activity) // Create Volley request queue
            val url = Urls.landlordEnquiriesUrl + "?id=${landlordId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val enquiriesData = obj.getJSONArray("enquiries") // Get enquiries from backend as JSON array
                        // Use GSON library to convert JSON array into List of Enquiry type
                        val gson = Gson()
                        val enquiryType = object : TypeToken<List<Enquiry>>() {}.type
                        val enquiries = gson.fromJson<List<Enquiry>>(enquiriesData.toString(), enquiryType)

                        enquiriesAdapter = UserEnquiriesListAdapter(enquiries) // Set enquiries data to recycler view adapter

                        rcView.adapter = enquiriesAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle error message and show in toast
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
            // Navigate to home fragment if no data found
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LandlordHomeFragment()).commit()
        }

        return view
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}