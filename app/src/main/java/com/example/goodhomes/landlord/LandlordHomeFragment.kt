package com.example.goodhomes.landlord

import android.os.Bundle
import android.util.Log
import android.util.Property
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.example.goodhomes.admin.Landlord
import com.example.goodhomes.admin.LandlordListAdapter
import com.example.goodhomes.common.PropertyListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

class LandlordHomeFragment : Fragment() {
    private lateinit var propertiesAdapter: PropertyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_landlord_home, container, false)

        val searchView = view.findViewById<SearchView>(R.id.search_properties)
        val rcView = view.findViewById<RecyclerView>(R.id.rcView)

        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        // Get landlord id from the SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        val landlordId = sharedPreferences?.getInt("userId", -1)

        val queue = Volley.newRequestQueue(activity)
        val url = Urls.landlordHomeUrl + "?deviceType=mobile&id=${landlordId}"
        Log.i("URL: ", url)
        val stringRequest = object: StringRequest(
            Method.GET,
            url,
            Response.Listener { response ->
                try {
                    Log.i("Response:", response)
                    val obj = JSONObject(response)
                    val myPropertiesData = obj.getJSONArray("landlordPropertyDetails") // Get landlord's properties from backend as JSON array

                    // Use GSON library to convert JSON array into List of type Property
                    val gson = Gson()
                    val propertyType = object : TypeToken<List<com.example.goodhomes.common.Property>>() {}.type
                    val myProperties = gson.fromJson<List<com.example.goodhomes.common.Property>>(myPropertiesData.toString(), propertyType)

                    propertiesAdapter = PropertyListAdapter(myProperties, this)

                    rcView.adapter = propertiesAdapter

                    // Listener for search bar text input
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        // Filter landlord's properties based on query
                        override fun onQueryTextChange(newText: String?): Boolean {
                            val filteredList = myProperties.filter {
                                it.name.contains(newText ?: "", ignoreCase = true)
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

    private fun updateAdapters(filteredList: List<com.example.goodhomes.common.Property>) {
        propertiesAdapter.updateData(filteredList)
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}