package com.alif.dicodingevent.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.alif.dicodingevent.R
import com.alif.dicodingevent.databinding.ActivityMainBinding
import com.alif.dicodingevent.ui.view_model.SettingViewModel
import com.alif.dicodingevent.ui.view_model.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val settingViewModel: SettingViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_active_event,
                R.id.navigation_finished_event,
                R.id.navigation_favorite_event
            )
        )

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        val navView = binding.navView

        navView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setTitleTextColor(getResources().getColor(R.color.white))

        // Untuk menghilangkan bottom navigation pada DetailFragment atau SettingFragment jika diinginkan
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isBottomNavVisible = when (destination.id) {
                R.id.detailFragment, R.id.navigation_setting -> false
                else -> true
            }
            setBottomNavigationVisibility(isBottomNavVisible)
        }

        settingViewModel.themeSetting.observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container) as NavHostFragment
        val navController = navHostFragment.navController
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.navView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}