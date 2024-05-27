package com.example.goodhomes.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.goodhomes.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class Dashboard_Customer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_customer)

        val bottomNavigationView = findViewById<ChipNavigationBar>(R.id.customer_bottomNav) // Get the bottom navigation in a variable

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CustomerHomeFragment()).commit() // Set the starting fragment to be home fragment

        bottomNavigationView.setOnItemSelectedListener { item -> // Set a listener for bottom navigation's item change
            var selectedFragment: Fragment? = null
            when (item) { // Set the new fragment based on item's id
                R.id.home_bottomNav -> selectedFragment = CustomerHomeFragment()
                R.id.enquiries_bottomNav -> selectedFragment = CustomerEnquiriesFragment()
                R.id.profile_bottomNav -> selectedFragment = CustomerProfileFragment()
            }
            // Replace current fragment with the new fragment
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            }
        }

        if (savedInstanceState == null) {
            bottomNavigationView.setItemSelected(R.id.home_bottomNav) // Set default selected item of bottom navigation to home
        }
    }
}