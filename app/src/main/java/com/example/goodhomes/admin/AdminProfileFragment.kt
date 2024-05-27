package com.example.goodhomes.admin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.MainActivity
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.example.goodhomes.common.WebViewFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject

class AdminProfileFragment: Fragment() {
    private var adminId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_admin_profile, container, false)

        // Get current logged in user's dat from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        adminId = sharedPreferences?.getInt("userId", -1)
        val adminName = sharedPreferences?.getString("userName", null)
        val adminEmail = sharedPreferences?.getString("userEmail", null)

        // If data is found
        if (adminId != -1 && adminName != null && adminEmail != null) {
            // Create Volley request queue
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.adminProfileUrl + "?id=${adminId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = @SuppressLint("SetTextI18n")
            object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        // Variable to hold user data from backend
                        val adminDetails = obj.getJSONObject("adminDetails")

                        // Get views in these variables and assign data that is coming from backend
                        val usernameLbl = view.findViewById<TextView>(R.id.usernameLbl)
                        val emailLbl = view.findViewById<TextView>(R.id.emailLbl)
                        val txtName = view.findViewById<EditText>(R.id.txtName)
                        val txtEmail = view.findViewById<EditText>(R.id.txtEmail)

                        usernameLbl.text = adminDetails.getString("fullName")
                        emailLbl.text = adminDetails.getString("email")
                        txtName.setText(adminDetails.getString("fullName"))
                        txtEmail.setText(adminDetails.getString("email"))
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
                .replace(R.id.fragment_container, AdminHomeFragment()).commit()
        }

        // Variables to hold update and logout button
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val btnWebView = view.findViewById<Button>(R.id.btnWebView)

        btnWebView.setOnClickListener {
            // Go to the web view fragment on button click
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WebViewFragment()).commit()
        }

        btnUpdate.setOnClickListener {
            updateAdmin()
        }

        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    // Function to update the user's data
    private fun updateAdmin() {
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
            // Create Volley request queue
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.adminProfileUrl + "?deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val message = obj.getString("message")
                        // Show message from backend in toast
                        toastMessage(activity, message)

                        // Reload the fragment
                        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                            .detach(this).attach(this).commit()
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
                    // Create HashMap and put the data in it
                    val params = HashMap<String, String>()
                    params["adminId"] = adminId.toString()
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