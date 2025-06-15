package com.logicline.mydining.ui.activities

import android.content.Intent // Import Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.logicline.mydining.R
import com.logicline.mydining.ui.custom.monthpicker.MonthPickerDialog
import com.logicline.mydining.ui.fragments.menus.HomeFragment
// Remove MessFragment import if it's no longer needed elsewhere
// import com.logicline.mydining.ui.fragments.menus.MessFragment
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
            // If the restored tab is nav_mess, we don't need to load a fragment,
            // as it would have launched an activity.
            // However, to maintain consistency with the BottomNav selection,
            // we still set it. The actual navigation to MessInfoActivity
            // would have happened before the state was saved if it was the active item.
            bottomNav.selectedItemId = currentFragmentId
            if (currentFragmentId != R.id.nav_mess) {
                loadFragment(getOrCreateFragmentById(currentFragmentId))
            }
        } else {
            // Only add fragment if this is the first creation, not a recreation
            if (currentFragmentId != R.id.nav_mess) {
                loadFragment(getOrCreateFragmentById(currentFragmentId))
            } else {
                // If the default is nav_mess, launch the activity directly
                startActivity(Intent(this, MessInfoActivity::class.java))
                // Optionally, you might want to select a default fragment tab
                // if MessInfoActivity is launched, or handle the back stack appropriately.
                // For now, let's assume if nav_mess is default, we just launch it.
            }
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

            if (fragmentId == R.id.nav_mess) {
                // If nav_mess is clicked, start MessInfoActivity
                startActivity(Intent(this, MessInfoActivity::class.java))
                false
            } else {
                // For other items, load the fragment as before
                currentFragmentId = fragmentId
                loadFragment(getOrCreateFragmentById(fragmentId))
                true
            }
        }
    }

    private fun getOrCreateFragmentById(fragmentId: Int): Fragment {
        // Get existing fragment or create a new one
        return fragmentMap.getOrPut(fragmentId) {
            when (fragmentId) {
                R.id.nav_home -> HomeFragment()
                // R.id.nav_mess case is handled in setupBottomNavigation for starting an Activity.
                // If you still need a placeholder fragment for some reason, you can keep it,
                // but it won't be displayed when nav_mess is clicked.
                // For clarity, it's better to remove it if nav_mess always opens an activity.
                // R.id.nav_mess -> MessFragment()
                R.id.nav_month -> MonthFragment()
                else -> HomeFragment() // Default fragment
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        for (existingFragment in fragmentMap.values) {
            if (supportFragmentManager.fragments.contains(existingFragment)) {
                transaction.hide(existingFragment)
            }
        }

        if (!supportFragmentManager.fragments.contains(fragment)) {
            transaction.add(R.id.fragment_container, fragment)
        } else {
            transaction.show(fragment)
        }

        transaction.commit()
    }
}