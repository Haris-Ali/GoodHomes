package com.example.goodhomes.customer

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
import com.example.goodhomes.common.MyEnquiriesListAdapter
import com.example.goodhomes.common.MyEnquiry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

class CustomerEnquiriesFragment : Fragment() {
    private lateinit var enquiriesAdapter: MyEnquiriesListAdapter
    private var userId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_enquiries, container, false)

        // Get user data from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        userId = sharedPreferences?.getInt("userId", -1) // Get user id and assign to variable

        // Get recycler view from the view using findViewById
        val rcView = view.findViewById<RecyclerView>(R.id.rcView)
        rcView.layoutManager = LinearLayoutManager(activity)

        // If user id is found
        if (userId != -1) {
            val queue = Volley.newRequestQueue(activity) // Create Volley request queue
            val url = Urls.customerEnquiriesUrl + "?id=${userId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        // Get all enquiries data from JSON array and use GSON library to convert it into List
                        val enquiriesData = obj.getJSONArray("enquiries")
                        val gson = Gson()
                        val enquiryType = object : TypeToken<List<MyEnquiry>>() {}.type
                        val enquiries = gson.fromJson<List<MyEnquiry>>(enquiriesData.toString(), enquiryType)

                        enquiriesAdapter = MyEnquiriesListAdapter(enquiries) // Set enquiries data to recycler view adapter

                        rcView.adapter = enquiriesAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle error message and display in a toast
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
            // If no data is found go back to home fragment
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomerHomeFragment()).commit()
        }

        return view
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}