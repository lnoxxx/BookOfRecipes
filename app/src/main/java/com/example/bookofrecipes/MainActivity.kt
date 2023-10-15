package com.example.bookofrecipes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        navigationActive()

    }

    private fun navigationActive(){
        val navController = findNavController(R.id.fragmentContainerView2)
        val conf = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.addFragment,
                R.id.ingredientsFragment,
                R.id.categoriesFragment
            )
        )
        setupActionBarWithNavController(navController, conf)
        binding.navController.setupWithNavController(navController)
    }

}