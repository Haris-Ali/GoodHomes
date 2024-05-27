package com.example.goodhomes.landlord

import android.annotation.SuppressLint
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
import androidx.fragment.app.FragmentActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.goodhomes.R
import com.example.goodhomes.Urls
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject

class PropertyDetailsFragment : Fragment() {
    private var propertyId: Int? = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_property_details, container, false)

        val btnDeleteProperty = view.findViewById<Button>(R.id.btnDeleteProperty)
        val btnUpdateProperty = view.findViewById<Button>(R.id.btnUpdateProperty)

        val sharedPreferences = activity?.getSharedPreferences("property", AppCompatActivity.MODE_PRIVATE)
        propertyId = sharedPreferences?.getInt("propertyId", -1)

        if (propertyId != -1) {
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.propertyDetailsUrl + "?id=${propertyId}&deviceType=mobile"
            Log.i("URL: ", url)
            val stringRequest = @SuppressLint("SetTextI18n")
            object: StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.i("Response:", response)
                        val obj = JSONObject(response)
                        val propertyDetails = obj.getJSONObject("propertyDetails")

                        val txtName = view.findViewById<EditText>(R.id.txtName)
                        val txtLocation = view.findViewById<EditText>(R.id.txtLocation)
                        val txtDescription = view.findViewById<EditText>(R.id.txtDescription)
                        val txtPrice = view.findViewById<EditText>(R.id.txtPrice)

                        txtName.setText(propertyDetails.getString("name"))
                        txtLocation.setText(propertyDetails.getString("location"))
                        txtDescription.setText(propertyDetails.getString("description"))
                        txtPrice.setText(propertyDetails.getString("price"))
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
        } else {
            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LandlordHomeFragment()).commit()
        }

        btnDeleteProperty.setOnClickListener { v -> deleteProperty(v) }

        btnUpdateProperty.setOnClickListener { v -> updateProperty(v) }

        return view
    }

    private fun deleteProperty(v: View) {
        MaterialAlertDialogBuilder(v.context)
            .setTitle(resources.getString(R.string.deleteProperty_card_title))
            .setMessage(resources.getString(R.string.deleteProperty_card_msg))
            .setNeutralButton(resources.getString(R.string.cancelBtn_Text)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.deleteBtn_text)) { dialog, which ->
                val queue = Volley.newRequestQueue(activity)
                val url = Urls.propertyDetailsUrl + "?deviceType=mobile"
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
                            dialog.dismiss()
                            (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, LandlordHomeFragment()).commit()
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
                        val params = HashMap<String, String>()
                        params["propertyId"] = propertyId.toString()
                        params["delete"] = "delete"
                        params["deviceType"] = "mobile"
                        return params
                    }
                }
                queue.add(stringRequest)
            }
            .show()
    }

    private fun updateProperty(v: View) {
        val txtNameLayout = view?.findViewById<TextInputLayout>(R.id.txtNameLayout)
        val txtLocationLayout = view?.findViewById<TextInputLayout>(R.id.txtLocationLayout)
        val txtDescriptionLayout = view?.findViewById<TextInputLayout>(R.id.txtDescriptionLayout)
        val txtPriceLayout = view?.findViewById<TextInputLayout>(R.id.txtPriceLayout)
        val txtName = view?.findViewById<EditText>(R.id.txtName)
        val txtLocation = view?.findViewById<EditText>(R.id.txtLocation)
        val txtDescription = view?.findViewById<EditText>(R.id.txtDescription)
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
            val queue = Volley.newRequestQueue(activity)
            val url = Urls.propertyDetailsUrl + "?deviceType=mobile"
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
                        (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, LandlordHomeFragment()).commit()
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
                    val params = HashMap<String, String>()
                    params["propertyId"] = propertyId.toString()
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