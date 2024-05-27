package com.example.goodhomes.customer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.Notifications
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class EnquiryDialogFragment(private var propertyId: Int?, private var userId: Int?) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE) // Remove the title from the dialog
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enquiry_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view items from xml using findViewById method
        val txtEnquiryMsgLayout = view.findViewById<TextInputLayout>(R.id.txtEnquiryMsgLayout)
        val txtEnquiryMsg = view.findViewById<EditText>(R.id.txtEnquiryMsg)
        val btnSendEnquiry = view.findViewById<Button>(R.id.btnSendEnquiry)
        val btnGoBack = view.findViewById<Button>(R.id.btnGoBack)

        // Set listener for enquiry button
        btnSendEnquiry.setOnClickListener { v ->
            val enquiryMsg = txtEnquiryMsg.text.toString()

            // If text field is empty, display the error
            if (enquiryMsg.isEmpty()) {
                txtEnquiryMsgLayout.error = view.context.getString(R.string.enquiryMsg_error)
            } else {
                sendEnquiry(enquiryMsg, v) // Call function to send enquiry
            }
        }

        btnGoBack.setOnClickListener { dismiss() } // Button to close the dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) // Method to set width and height of the dialog
    }

    private fun sendEnquiry(msg: String, v: View) {
        val queue = Volley.newRequestQueue(activity) // Create Volley request queue
        val url = Urls.propertyDetailsForCustomerUrl + "?deviceType=mobile"
        Log.i("URL: ", url)
        val stringRequest = object: StringRequest(
            Method.POST,
            url,
            Response.Listener { response ->
                try {
                    Log.i("Response:", response)
                    val obj = JSONObject(response)
                    val message = obj.getString("message")
                    toastMessage(activity, message) // Show message in the toast

                    // Creating notification for enquiry
                    val builder = NotificationCompat.Builder(v.context, Notifications.CHANNEL_ID_PROPERTY_ENQUIRY)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("New Property Enquiry") // Set the title of notification
                        .setContentText("A customer has enquired about your property") // Set notification message
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    with (NotificationManagerCompat.from(v.context)){
                        // Check if user has granted notification permission.
                        // Get user permission, if already granted post the notification
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

                    dismiss()
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
                // Put POST data in a HashMap
                val params = HashMap<String, String>()
                params["userId"] = userId.toString()
                params["propertyId"] = propertyId.toString()
                params["message"] = msg
                params["submit"] = "submit"
                params["deviceType"] = "mobile"
                return params
            }
        }
        queue.add(stringRequest)
    }

    private fun toastMessage(activity: FragmentActivity?, msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }
}