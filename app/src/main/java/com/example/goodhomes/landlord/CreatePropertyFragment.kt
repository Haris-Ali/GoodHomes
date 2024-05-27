package com.example.goodhomes.landlord

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.Notifications
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.example.goodhomes.admin.AdminHomeFragment
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class CreatePropertyFragment : Fragment() {
    private var landlordId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_create_property, container, false)

        val btnGoBack = view.findViewById<Button>(R.id.btnGoBack) // Button variable for go back button
        val btnCreateProperty = view.findViewById<Button>(R.id.btnCreateProperty) // Button variable for create property button

        // Get landlord id from SharedPreferences
        val sharedPreferences = activity?.getSharedPreferences("User", AppCompatActivity.MODE_PRIVATE)
        landlordId = sharedPreferences?.getInt("userId", -1)

        // Navigate to home fragment on go back button listener
        btnGoBack.setOnClickListener {
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LandlordHomeFragment()).commit()
        }

        btnCreateProperty.setOnClickListener { v ->
            createProperty(v)
        }

        return view
    }

    // Function to create property
    private fun createProperty(v: View) {
        // Variables to hold input field views from xml file
        val txtNameLayout = view?.findViewById<TextInputLayout>(R.id.txtNameLayout)
        val txtName = view?.findViewById<EditText>(R.id.txtName)
        val txtLocationLayout = view?.findViewById<TextInputLayout>(R.id.txtLocationLayout)
        val txtLocation = view?.findViewById<EditText>(R.id.txtLocation)
        val txtDescriptionLayout = view?.findViewById<TextInputLayout>(R.id.txtDescriptionLayout)
        val txtDescription = view?.findViewById<EditText>(R.id.txtDescription)
        val txtPriceLayout = view?.findViewById<TextInputLayout>(R.id.txtPriceLayout)
        val txtPrice = view?.findViewById<EditText>(R.id.txtPrice)

        if (txtName?.text.toString().isEmpty() || txtLocation?.text.toString().isEmpty()
            || txtDescription?.text.toString().isEmpty() || txtPrice?.text.toString().isEmpty()) {
            toastMessage(activity, getString(R.string.missingFieldsError))
            if (txtName?.text.toString().isEmpty()) {
                txtNameLayout?.error = getString(R.string.nameError)
            } else {
                txtNameLayout?.error = null
            }
            if (txtLocation?.text.toString().isEmpty()) {
                txtLocationLayout?.error = getString(R.string.locationError)
            } else {
                txtLocationLayout?.error = null
            }
            if (txtDescription?.text.toString().isEmpty()) {
                txtDescriptionLayout?.error = getString(R.string.descriptionError)
            } else {
                txtDescriptionLayout?.error = null
            }
            if (txtPrice?.text.toString().isEmpty()) {
                txtPriceLayout?.error = getString(R.string.priceError)
            } else {
                txtPriceLayout?.error = null
            }
        } else {
            txtNameLayout?.error = null
            txtLocationLayout?.error = null
            txtDescriptionLayout?.error = null
            txtPriceLayout?.error = null
            val url = Urls.createPropertyUrl + "?deviceType=mobile"
            Log.i("URL: ", url)
            val queue = Volley.newRequestQueue(activity) // Create Volley request queue
            val stringRequest = object: StringRequest(
                Method.POST,
                url,
                Response.Listener{ response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val message = obj.getString("message")
                        toastMessage(activity, message) // Show message in toast

                        // Creating notification for new property creation
                        val builder = NotificationCompat.Builder(v.context, Notifications.CHANNEL_ID_CREATE_PROPERTY)
                            .setSmallIcon(android.R.drawable.ic_dialog_info)
                            .setContentTitle("New Property Created") // Set notification title
                            .setContentText("A new property has been created") // Set notification message
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        with (NotificationManagerCompat.from(v.context)){
                            // Check for user permission, if granted then show notification else ask for permission
                            if (ActivityCompat.checkSelfPermission(
                                    v.context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                ActivityCompat.requestPermissions(
                                    requireActivity(),
                                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                    123 // Request code, you can define your own
                                )
                            } else {
                                notify(0, builder.build())
                            }
                        }
                        // Navigate to home fragment finally
                        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, LandlordHomeFragment()).commit()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
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
                    toastMessage(activity, errorMessage)
                    when (errorMessage) {
                        "Error occurred while creating property" -> {
                            txtNameLayout?.error = errorMessage
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    // Put POST data in a HashMap
                    val params = HashMap<String, String>()
                    params["landlordId"] = landlordId.toString()
                    params["name"] = txtName?.text.toString()
                    params["location"] = txtLocation?.text.toString()
                    params["description"] = txtDescription?.text.toString()
                    params["price"] = txtPrice?.text.toString()
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