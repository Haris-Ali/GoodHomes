package com.example.goodhomes.customer

import android.app.UiModeManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.MainActivity
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.example.goodhomes.common.WebViewFragment
import com.example.goodhomes.landlord.LandlordHomeFragment
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class CustomerProfileFragment : Fragment() {
    private var userId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_customer_profile, container, false)

        // Get current logged in user's dat from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        userId = sharedPreferences?.getInt("userId", -1)
        val userName = sharedPreferences?.getString("userName", null)
        val userEmail = sharedPreferences?.getString("userEmail", null)

        // If data is found
        if (userId != -1 && userName != null && userEmail != null) {
            // Create Volley request queue
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.customerProfileUrl + "?id=${userId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val userDetails = obj.getJSONObject("userDetails") // Variable to hold user data from backend

                        // Get views in these variables and assign data that is coming from backend
                        val usernameLbl = view.findViewById<TextView>(R.id.usernameLbl)
                        val emailLbl = view.findViewById<TextView>(R.id.emailLbl)
                        val txtName = view.findViewById<EditText>(R.id.txtName)
                        val txtEmail = view.findViewById<EditText>(R.id.txtEmail)

                        usernameLbl.text = userDetails.getString("fullName")
                        emailLbl.text = userDetails.getString("email")
                        txtName.setText(userDetails.getString("fullName"))
                        txtEmail.setText(userDetails.getString("email"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle error message and display in toast
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
            // If no data is found, redirect to home fragment
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CustomerHomeFragment()).commit()
        }

        val btnWebView = view.findViewById<Button>(R.id.btnWebView)
        // Go to the web view fragment on button click
        btnWebView.setOnClickListener {
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WebViewFragment()).commit()
        }

        // Variables to hold update and logout button
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        btnUpdate.setOnClickListener {
            updateCustomer()
        }

        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun updateCustomer() { // Function to update the user's data
        // Get user name and email from view
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
            val queue = Volley.newRequestQueue(activity) // Create Volley request queue
            val url = Urls.customerProfileUrl + "?deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val message = obj.getString("message")
                        toastMessage(activity, message) // Show message from backend in toast
                        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                            .detach(this).attach(this).commit() // Reload the fragment
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.e("Error:", error.toString())
                    error.printStackTrace()
                    // Handle the error message and display in toast
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
                    val params = HashMap<String, String>()
                    // Create HashMap and put the data in it
                    params["userId"] = userId.toString()
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

    private fun logout() {
        // Get SharedPreferences and clear them before redirecting to MainActivity
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences?.edit()?.clear()?.apply()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

}