package com.example.goodhomes.common

// Data class for enquiry object viewed by landlord
data class Enquiry(
    val enquiry_id: Int,
    val user_fullName: String,
    val property_name: String,
    val user_email: String,
    val message: String,
    val landlord_id: Int,
    val landlord_fullName: String,
    val landlord_email: String
)
