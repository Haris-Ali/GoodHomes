package com.example.goodhomes.admin

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

class AdminHomeFragment: Fragment() {
    // Variables to hold our two recycler views
    private lateinit var topLandlordListAdapter: LandlordListAdapter
    private lateinit var allLandlordListAdapter: LandlordListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        // Variables to hold view items
        val searchView = view.findViewById<SearchView>(R.id.search_landlord)
        val rcViewTopLandlords = view.findViewById<RecyclerView>(R.id.rcViewTopLandlords)
        val rcViewAllLandlords = view.findViewById<RecyclerView>(R.id.rcViewAllLandlords)

        // Set layout for the two recycler views
        rcViewTopLandlords.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rcViewAllLandlords.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // Create Volley request queue
        val queue = Volley.newRequestQueue(activity)
        val url = Urls.landlordsAdminUrl + "?deviceType=mobile"
        Log.i("URL: ", url)
        val stringRequest = object: StringRequest(
            Method.GET,
            url,
            Response.Listener { response ->
                try {
                    Log.i("Response:", response)
                    val obj = JSONObject(response)
                    // Get top and all landlords from backend
                    val topLandlordsData = obj.getJSONArray("topLandlords")
                    val allLandlordsData = obj.getJSONArray("allLandlords")

                    // Use GSON library to convert JSON arrays to List
                    val gson = Gson()
                    val landlordType = object : TypeToken<List<Landlord>>() {}.type
                    val topLandlords = gson.fromJson<List<Landlord>>(topLandlordsData.toString(), landlordType)
                    val allLandlords = gson.fromJson<List<Landlord>>(allLandlordsData.toString(), landlordType)

                    // Assign data to our recycler views
                    topLandlordListAdapter = LandlordListAdapter(topLandlords)
                    allLandlordListAdapter = LandlordListAdapter(allLandlords)

                    rcViewTopLandlords.adapter = topLandlordListAdapter
                    rcViewAllLandlords.adapter = allLandlordListAdapter

                    // Function to handle search bar's input
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        // Filter based on the landlords' name
                        override fun onQueryTextChange(newText: String?): Boolean {
                            val filteredList = allLandlords.filter {
                                it.fullName.contains(newText ?: "", ignoreCase = true)
                            }
                            updateAdapters(filteredList)
                            return true
                        }
                    })
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Log.e("Error:", error.toString())
                error.printStackTrace()
                // Get error message from backend and display in toast
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

        return view
    }

    private fun updateAdapters(filteredList: List<Landlord>) {
        // Update adapter function to update view when search input is handled
        val filteredTopLandlords = filteredList.sortedByDescending { it.property_count }.take(3)
        topLandlordListAdapter.updateData(filteredTopLandlords)
        allLandlordListAdapter.updateData(filteredList)
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}