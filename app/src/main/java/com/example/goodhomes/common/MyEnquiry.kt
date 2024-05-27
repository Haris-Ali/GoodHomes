package com.example.goodhomes.common

// Data class for customer's enquiry object
data class MyEnquiry(
    val enquiry_id: Int,
    val enquiry_message: String,
    val property_id: Int,
    val property_name: String,
    val property_location: String,
    val landlord_id: Int,
    val landlord_fullName: String,
    val landlord_email: String,
    val user_fullName: String,
    val user_email: String
)
