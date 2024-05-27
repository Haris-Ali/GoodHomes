package com.example.goodhomes.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.goodhomes.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class Dashboard_Admin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_admin)

        // Get the bottom navigation in a variable
        val bottomNavigationView = findViewById<ChipNavigationBar>(R.id.admin_bottomNav)

        // Set the starting fragment to be home fragment
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AdminHomeFragment()).commit()

        // Set a listener for bottom navigation's item change
        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            // Set the new fragment based on item's id
            when (item) {
                R.id.home_bottomNav -> selectedFragment = AdminHomeFragment()
                R.id.create_bottomNav -> selectedFragment = CreateLandlordFragment()
                R.id.profile_bottomNav -> selectedFragment = AdminProfileFragment()
            }
            // Replace current fragment with the new fragment
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()
            }
        }

        // Set default selected item of bottom navigation to home
        if (savedInstanceState == null) {
            bottomNavigationView.setItemSelected(R.id.home_bottomNav)
        }
    }
}