package com.example.goodhomes.admin

// Data class for the Landlord with id, fullName, email and property_count
// (Used in admin's home dashboard to display list of landlords)
data class Landlord(
    val id: Int,
    val fullName: String,
    val email: String,
    val property_count: Int
)
