package com.alif.dicodingevent.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.alif.dicodingevent.R
import com.alif.dicodingevent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.navView
        val navController = findNavController(R.id.container)

        navView.setupWithNavController(navController)

        // Untuk menghilangkan bottom navigation pada DetailFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            setBottomNavigationVisibility(destination.id != R.id.detailFragment)
        }
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        binding.navView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}