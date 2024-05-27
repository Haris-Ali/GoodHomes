package com.example.goodhomes

// Here we are storing all the backend URLs that are being accessed in our app
object Urls {
    private const val BASEURL = "http://10.128.29.135/"

    const val loginUrl = BASEURL + "goodhomes/auth/login.php"
    const val signupUrl = BASEURL + "goodhomes/auth/signup.php"
    const val landlordsAdminUrl = BASEURL + "goodhomes/admin/dashboard.php"
    const val landlordProfileAdminUrl = BASEURL + "goodhomes/admin/landlordProfile.php"
    const val createLandlordUrl = BASEURL + "goodhomes/admin/createLandlord.php"
    const val adminProfileUrl = BASEURL + "goodhomes/admin/profile.php"

    const val landlordHomeUrl = BASEURL + "goodhomes/landlord/dashboard.php"
    const val propertyDetailsUrl = BASEURL + "goodhomes/landlord/propertyDetails.php"
    const val createPropertyUrl = BASEURL + "goodhomes/landlord/createProperty.php"
    const val landlordEnquiriesUrl = BASEURL + "goodhomes/landlord/enquiries.php"
    const val landlordProfileUrl = BASEURL + "goodhomes/landlord/profile.php"

    const val customerHomeUrl = BASEURL + "goodhomes/customer/dashboard.php"
    const val propertyDetailsForCustomerUrl = BASEURL + "goodhomes/customer/propertyDetails.php"
    const val customerEnquiriesUrl = BASEURL + "goodhomes/customer/enquiries.php"
    const val customerProfileUrl = BASEURL + "goodhomes/customer/profile.php"
}