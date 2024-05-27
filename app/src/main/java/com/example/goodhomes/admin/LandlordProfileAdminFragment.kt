package com.example.goodhomes.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import com.example.goodhomes.common.Property
import com.example.goodhomes.common.PropertyListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject


class LandlordProfileAdminFragment : Fragment() {
    private lateinit var propertyListAdapter: PropertyListAdapter
    private var landlordId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_landlord_profile_admin, container, false)

        // Get the current landlord's details from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("landlord", AppCompatActivity.MODE_PRIVATE)
        landlordId = sharedPreferences?.getInt("landlordId", -1)
        val landlordName = sharedPreferences?.getString("landlordName", null)
        val landlordEmail = sharedPreferences?.getString("landlordEmail", null)

        // If details are found
        if (landlordId != -1 && landlordName != null && landlordEmail != null) {
            // Fetch landlord and landlords' properties
            // Create Volley request queue
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.landlordProfileAdminUrl + "?id=${landlordId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = @SuppressLint("SetTextI18n")
            object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        // Get landlord's details and landlord's properties from the backend
                        val landlordDetails = obj.getJSONObject("landlordDetails")
                        val landlordPropertyDetails = obj.getJSONArray("landlordPropertyDetails")

                        // Variables to hold view items
                        val usernameLbl = view.findViewById<TextView>(R.id.usernameLbl)
                        val emailLbl = view.findViewById<TextView>(R.id.emailLbl)
                        val txtName = view.findViewById<EditText>(R.id.txtName)
                        val txtEmail = view.findViewById<EditText>(R.id.txtEmail)
                        val propertiesLbl = view.findViewById<TextView>(R.id.propertiesLbl)

                        // Assign landlord's details to appropriate view items
                        usernameLbl.text = landlordDetails.getString("fullName")
                        emailLbl.text = landlordDetails.getString("email")
                        txtName.setText(landlordDetails.getString("fullName"))
                        txtEmail.setText(landlordDetails.getString("email"))
                        propertiesLbl.text = "${landlordDetails.getString("fullName")}'s ${view.context.getString(R.string.propertiesWithLandlordName_lbl)}"

                        // Get recycler view for landlord's properties
                        val rcView = view.findViewById<RecyclerView>(R.id.rcView)
                        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

                        // Use GSON library to convert JSON array to List
                        val gson = Gson()
                        val propertyType = object : TypeToken<List<Property>>() {}.type
                        val properties = gson.fromJson<List<Property>>(landlordPropertyDetails.toString(), propertyType)

                        // Set properties data to the recycler view's adapter
                        propertyListAdapter = PropertyListAdapter(properties, this)
                        rcView.adapter = propertyListAdapter
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle error message from backend and show in toast
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
            // If no data is found, navigate back to home fragment
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminHomeFragment()).commit()
        }

        // Get buttons for delete and update and set on click listeners
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)

        btnDelete.setOnClickListener { v ->
            deleteLandlord(v)
        }

        btnUpdate.setOnClickListener { v ->
            updateLandlord(v)
        }

        return view
    }

    // A function to delete the current landlord
    private fun deleteLandlord(v: View) {
        // Create a dialog to get admin's confirmation
        MaterialAlertDialogBuilder(v.context)
            .setTitle(resources.getString(R.string.deleteLandlord_card_title)) // Setting dialog's title
            .setMessage(resources.getString(R.string.deleteLandlord_card_msg)) // Setting dialog's message
            .setNeutralButton(resources.getString(R.string.cancelBtn_Text)) { dialog, which -> // Close dialog if cancel button is pressed
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.deleteBtn_text)) { dialog, which ->
                // Create Volley request queue
                val queue = Volley.newRequestQueue(activity)
                val url = Urls.landlordProfileAdminUrl + "?deviceType=mobile"
                Log.i("URL: ", url)
                val stringRequest = object: StringRequest(
                    Method.POST,
                    url,
                    Response.Listener { response ->
                        try {
                            Log.i("Response:", response)
                            val obj = JSONObject(response)
                            // Show message from backend in a toast
                            val message = obj.getString("message")
                            toastMessage(activity, message)
                            dialog.dismiss() // Close the dialog
                            // Navigate to home fragment
                            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, AdminHomeFragment()).commit()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.e("Error:", error.toString())
                        error.printStackTrace()
                        // Handling the error message from backend
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
                        toastMessage(activity, errorMessage) // Show error message in a toast
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        // Put POST data in HashMap
                        params["landlordId"] = landlordId.toString()
                        params["delete"] = "delete"
                        params["deviceType"] = "mobile"
                        return params
                    }
                }
                queue.add(stringRequest)
            }
            .show()
    }

    // A function to update landlord's details
    private fun updateLandlord(v: View) {
        // Get name and email of the landlord
        val txtNameLayout = activity?.findViewById<TextInputLayout>(R.id.txtNameLayout)
        val txtEmailLayout = activity?.findViewById<TextInputLayout>(R.id.txtEmailLayout)
        val txtName = activity?.findViewById<EditText>(R.id.txtName)
        val txtEmail = activity?.findViewById<EditText>(R.id.txtEmail)
        if (txtName?.text.toString().isEmpty() || txtEmail?.text.toString().isEmpty()) {
            toastMessage(activity, getString(R.string.missingFieldsError))
            if (txtName?.text.toString().isEmpty()) {
                txtNameLayout?.error = getString(R.string.nameError)
            } else {
                txtNameLayout?.error = null
            }

            if (txtEmail?.text.toString().isEmpty()) {
                txtEmailLayout?.error = getString(R.string.emailError)
            } else {
                txtEmailLayout?.error = null
            }
        } else {
            txtNameLayout?.error = null
            txtEmailLayout?.error = null
            // Create Volley request queue
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.landlordProfileAdminUrl + "?deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val message = obj.getString("message")
                        toastMessage(activity, message)
                        // Reload the current fragment
                        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                            .detach(this).attach(this).commit()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle error message from the backend
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
                    toastMessage(activity, errorMessage) // Show error msg in toast
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    // Put POST data in a HashMap
                    params["landlordId"] = landlordId.toString()
                    params["name"] = txtName?.text.toString()
                    params["email"] = txtEmail?.text.toString()
                    params["submit"] = "submit"
                    params["deviceType"] = "mobile"
                    return params
                }
            }
            queue.add(stringRequest) // Finally add request to queue
        }
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}