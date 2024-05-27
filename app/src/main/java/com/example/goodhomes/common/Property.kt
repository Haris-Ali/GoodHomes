package com.example.goodhomes.common

// Data class for property object
data class Property(
    val id: Int,
    val landlord_Id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val location: String,
    val is_available: Int,
    val pictures: String
)
