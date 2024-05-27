package com.example.goodhomes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.goodhomes.admin.Dashboard_Admin
import com.example.goodhomes.auth.LoginActivity
import com.example.goodhomes.customer.Dashboard_Customer
import com.example.goodhomes.landlord.Dashboard_Landlord

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkLoginStatus()
    }

    // A function invokedin the MainActivity to check if a user is logged in or not
    fun checkLoginStatus() {
        // Get the user data from SharedPreferences
        val sharedPreferences = getSharedPreferences("User", MODE_PRIVATE)
        // Variables to hold the user's id, type and name
        val userId = sharedPreferences.getInt("userId", -1)
        val userType = sharedPreferences.getString("userType", null)
        val userName = sharedPreferences.getString("userName", null)

        if (userId != -1 && userType != null && userName != null) {
            // If user data is stored, we can create the notification channel(s)
            createNotificationChannel()
            // Go to user page based on user type
            when (userType) {
                "ADMIN" -> navigateToDashboard(Dashboard_Admin::class.java, userId, userType, userName)
                "LANDLORD" -> navigateToDashboard(Dashboard_Landlord::class.java, userId, userType, userName)
                "CUSTOMER" -> navigateToDashboard(Dashboard_Customer::class.java, userId, userType, userName)
            }
        } else {
            // Navigate to the login page if no user is found in SharedPreferences
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    // A function that takes the Activity class and user data as input and redirects to corresponding Activity using an Intent
    private fun navigateToDashboard(dashboardClass: Class<*>, userId: Int, userType: String, userName: String) {
        val intent = Intent(this, dashboardClass)
        intent.putExtra("userId", userId)
        intent.putExtra("userType", userType)
        intent.putExtra("userName", userName)
        startActivity(intent)
    }

    // A function that creates the notification channel(s)
    private fun createNotificationChannel() {
        // Check if API version is above 31
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Variable for property creation channel (Getting the id and name from the Notifications object file)
            val createPropertyImportance = NotificationManager.IMPORTANCE_DEFAULT
            val createPropertyChannel =
                NotificationChannel(Notifications.CHANNEL_ID_CREATE_PROPERTY, Notifications.CHANNEL_NAME_CREATE_PROPERTY, createPropertyImportance).apply {  }

            // Variable for property enquiry channel (Getting the id and name from the Notifications object file)
            val propertyEnquiryImportance = NotificationManager.IMPORTANCE_DEFAULT
            val propertyEnquiryChannel =
                NotificationChannel(Notifications.CHANNEL_ID_PROPERTY_ENQUIRY, Notifications.CHANNEL_NAME_PROPERTY_ENQUIRY, propertyEnquiryImportance).apply {}

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(createPropertyChannel)
            notificationManager.createNotificationChannel(propertyEnquiryChannel)
        }
    }

}