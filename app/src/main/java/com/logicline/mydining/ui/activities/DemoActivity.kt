package com.logicline.mydining.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.logicline.mydining.R
import com.logicline.mydining.ui.custom.monthpicker.MonthPickerDialog
import com.logicline.mydining.ui.fragments.menus.HomeFragment
import com.logicline.mydining.ui.fragments.menus.MessFragment
import com.logicline.mydining.ui.fragments.menus.MonthFragment
import com.logicline.mydining.utils.AppPrefs
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "DemoActivity"
private const val KEY_SELECTED_TAB = "selected_tab"

@AndroidEntryPoint
class DemoActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var currentFragmentId = R.id.nav_home

    // Keep track of created fragments
    private val fragmentMap = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        bottomNav = findViewById(R.id.bottomNavigationView)
        setupBottomNavigation()

        // Restore selected tab or use default
        if (savedInstanceState != null) {
            currentFragmentId = savedInstanceState.getInt(KEY_SELECTED_TAB, R.id.nav_home)
            bottomNav.selectedItemId = currentFragmentId
        } else {
            // Only add fragment if this is the first creation, not a recreation
            loadFragment(getOrCreateFragmentById(currentFragmentId))
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save current tab selection
        outState.putInt(KEY_SELECTED_TAB, currentFragmentId)
        super.onSaveInstanceState(outState)
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { menuItem ->
            val fragmentId = menuItem.itemId
            currentFragmentId = fragmentId
            loadFragment(getOrCreateFragmentById(fragmentId))
            true
        }
    }

    private fun getOrCreateFragmentById(fragmentId: Int): Fragment {
        // Get existing fragment or create a new one
        return fragmentMap.getOrPut(fragmentId) {
            when (fragmentId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_mess -> MessFragment()
                R.id.nav_month -> MonthFragment()
                // Add more cases as needed
                else -> HomeFragment()
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // Use hide/show pattern instead of replace
        val transaction = supportFragmentManager.beginTransaction()

        // Hide all fragments first
        for (existingFragment in fragmentMap.values) {
            if (supportFragmentManager.fragments.contains(existingFragment)) {
                transaction.hide(existingFragment)
            }
        }

        // Add the fragment if it's not added yet, otherwise show it
        if (!supportFragmentManager.fragments.contains(fragment)) {
            transaction.add(R.id.fragment_container, fragment)
        } else {
            transaction.show(fragment)
        }

        transaction.commit()
    }
}