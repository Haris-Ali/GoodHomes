package com.example.goodhomes.landlord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.goodhomes.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class Dashboard_Landlord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_landlord)

        val bottomNavigationView = findViewById<ChipNavigationBar>(R.id.landlord_bottomNav) // Get the bottom navigation in a variable

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, LandlordHomeFragment()).commit() // Set the starting fragment to be home fragment

        // Set a listener for bottom navigation's item change
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item) { // Set the new fragment based on item's id
                R.id.home_bottomNav -> selectedFragment = LandlordHomeFragment()
                R.id.create_bottomNav -> selectedFragment = CreatePropertyFragment()
                R.id.enquiries_bottomNav -> selectedFragment = LandlordEnquiriesFragment()
                R.id.profile_bottomNav -> selectedFragment = LandlordProfileFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit() // Replace current fragment with the new fragment
            }
        }

        // Set default selected item of bottom navigation to home
        if (savedInstanceState == null) {
            bottomNavigationView.setItemSelected(R.id.home_bottomNav)
        }
    }
}