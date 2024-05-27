package com.example.goodhomes.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class CreateLandlordFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_landlord, container, false)

        // Create variables for back button and create button
        val btnGoBack = view.findViewById<Button>(R.id.btnGoBack)
        val btnCreateLandlord = view.findViewById<Button>(R.id.btnCreateLandlord)

        // Go back to home fragment on back button click
        btnGoBack.setOnClickListener {
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminHomeFragment()).commit()
        }

        btnCreateLandlord.setOnClickListener {
            createLandlord()
        }

        return view
    }

    // Function to create the landlord
    private fun createLandlord() {
        // Get the text fields and text field layouts from the xml file
        val txtNameLayout = view?.findViewById<TextInputLayout>(R.id.txtNameLayout)
        val txtName = view?.findViewById<EditText>(R.id.txtName)
        val txtEmailLayout = view?.findViewById<TextInputLayout>(R.id.txtEmailLayout)
        val txtEmail = view?.findViewById<EditText>(R.id.txtEmail)
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
            val url = Urls.createLandlordUrl
            Log.i("URL: ", url)
            // Create Volley request queue
            val queue = Volley.newRequestQueue(activity)
            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener{ response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val message = obj.getString("message")
                        // Show message in a toast
                        toastMessage(activity, message)
                        // Navigate to home fragment
                        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, AdminHomeFragment()).commit()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    // Handle error message from backend and display in toast
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
                    // Assign error's msg to appropriate text field
                    when (errorMessage) {
                        "User with this email already exists" -> {
                            txtEmailLayout?.error = errorMessage
                        }
                        "Error occurred while creating landlord" -> {
                            txtEmailLayout?.error = errorMessage
                            txtNameLayout?.error = errorMessage
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    // Put POST data in a HashMap
                    val params = HashMap<String, String>()
                    params["name"] = txtName?.text.toString()
                    params["email"] = txtEmail?.text.toString()
                    params["submit"] = "submit"
                    params["deviceType"] = "mobile"
                    return params
                }
            }
            queue.add(stringRequest)
        }
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}