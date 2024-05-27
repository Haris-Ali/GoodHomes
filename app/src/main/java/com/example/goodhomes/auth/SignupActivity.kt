package com.example.goodhomes.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.example.goodhomes.admin.Dashboard_Admin
import com.example.goodhomes.customer.Dashboard_Customer
import com.example.goodhomes.landlord.Dashboard_Landlord
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {
    private var selectedUserType: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Variables to get views for our login page
        var btnLogin = findViewById<TextView>(R.id.link_login)
        var btnSignup = findViewById<Button>(R.id.btnSignup)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_userType)

        // Setting listener for user type selection
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.radio_admin) {
                selectedUserType = "ADMIN"
            }
            if (checkedId == R.id.radio_landlord) {
                selectedUserType = "LANDLORD"
            }
            if (checkedId == R.id.radio_customer) {
                selectedUserType = "CUSTOMER"
            }
        }

        // Navigate to signup page if login button is clicked
        btnLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        // Set listener for signup button
        btnSignup.setOnClickListener {
            signup()
        }
    }

    private fun signup() {
        // Get the user inputs of email and password
        val txtNameLayout = findViewById<TextInputLayout>(R.id.txtNameLayout)
        val txtEmailLayout = findViewById<TextInputLayout>(R.id.txtEmailLayout)
        val txtPasswordLayout = findViewById<TextInputLayout>(R.id.txtPasswordLayout)
        val txtName = findViewById<EditText>(R.id.txtName)
        val txtEmail = findViewById<EditText>(R.id.txtEmail)
        val txtPassword = findViewById<EditText>(R.id.txtPassword)
        val userTypeErrorMsg = findViewById<TextView>(R.id.userTypeErrorMsg)
        val url = Urls.signupUrl
        // If user inputs are empty then throw an error in a toast message
        if (txtName.text.toString().isEmpty() || txtEmail.text.toString().isEmpty() || txtPassword.text.toString().isEmpty() || selectedUserType!!.isEmpty()) {
            toastMessage(getString(R.string.missingFieldsError))
            if (txtName.text.toString().isEmpty()) {
                txtNameLayout.error = getString(R.string.nameError)
            } else {
                txtNameLayout.error = null
            }

            if (txtEmail.text.toString().isEmpty()) {
                txtEmailLayout.error = getString(R.string.emailError)
            } else {
                txtEmailLayout.error = null
            }

            if (txtPassword.text.toString().isEmpty()) {
                txtPasswordLayout.error = getString(R.string.passwordError)
            } else {
                txtPasswordLayout.error = null
            }

            if (selectedUserType!!.isEmpty()) {
                userTypeErrorMsg.text = getString(R.string.userTypeError)
            } else {
                userTypeErrorMsg.text = null
            }
        } else {
            txtNameLayout.error = null
            txtEmailLayout.error = null
            txtPasswordLayout.error = null
            userTypeErrorMsg.text = null
            Log.i("URL: ", url)
            // Create Volley's request queue
            val queue = Volley.newRequestQueue(this)
            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener{ response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        // If signup is successful, retrieve user data from the backend
                        val message = obj.getString("message")
                        val userId = obj.getInt("user")
                        val userType = obj.getString("userType")
                        val userName = obj.getString("fullName")
                        val userEmail = obj.getString("email")
                        toastMessage(message)

                        // Create SharedPreferences for the user and put id, name, type and email in it
                        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putInt("userId", userId)
                        editor.putString("userType", userType)
                        editor.putString("userName", userName)
                        editor.putString("userEmail", userEmail)
                        editor.apply()

                        // Navigate to appropriate dashboard based on user type
                        when (userType) {
                            "ADMIN" -> navigateToDashboard(Dashboard_Admin::class.java, userId, userType, userName)
                            "LANDLORD" -> navigateToDashboard(Dashboard_Landlord::class.java, userId, userType, userName)
                            "CUSTOMER" -> navigateToDashboard(Dashboard_Customer::class.java, userId, userType, userName)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    // Getting the error message from the backend in case of error
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
                    toastMessage(errorMessage)
                    // Assigning error message to appropriate text field
                    val txtEmailLayout = findViewById<TextInputLayout>(R.id.txtEmailLayout)
                    val txtPasswordLayout = findViewById<TextInputLayout>(R.id.txtPasswordLayout)
                    when (errorMessage) {
                        "Incorrect password" -> {
                            txtPasswordLayout.error = errorMessage
                            txtEmailLayout.error = null
                        }
                        "User with this email already exists" -> {
                            txtEmailLayout.error = errorMessage
                            txtPasswordLayout.error = null
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    // Since this is a POST request, put the data in a HashMap and return it
                    val params = HashMap<String, String>()
                    params["name"] = txtName.text.toString()
                    params["email"] = txtEmail.text.toString()
                    params["password"] = txtPassword.text.toString()
                    params["userType"] = selectedUserType.toString()
                    params["submit"] = "submit"
                    params["deviceType"] = "mobile"
                    return params
                }
            }
            // Finally add the request to the queue
            queue.add(stringRequest)
        }
    }

    private fun toastMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDashboard(dashboardClass: Class<*>, userId: Int, userType: String, fullName: String) {
        val intent = Intent(this, dashboardClass)
        intent.putExtra("userId", userId)
        intent.putExtra("userType", userType)
        intent.putExtra("userName", fullName)
        startActivity(intent)
    }
}