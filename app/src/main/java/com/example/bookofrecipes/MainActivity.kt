package com.example.bookofrecipes

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        if ((binding.navController.visibility == View.VISIBLE) && (isVisible)){
            return
        }
        if ((binding.navController.visibility == View.GONE) && (!isVisible)){
            return
        }
        if (isVisible){
            binding.navController.visibility = View.VISIBLE
        } else {
            binding.navController.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        navigationActive()

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            fillDatabase()
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstRun", false)
            editor.apply()
        }

        binding.navController
    }

    private fun fillDatabase(){
        val app = applicationContext as Application
        val database = app.database
        val defaultIngredients = InitDatabase().ingredients
        CoroutineScope(Dispatchers.IO).launch {
            database.CategoryDao().insert(Category(name = "Без категории"))
            database.CategoryDao().insert(Category(name = "Завтраки"))
            database.CategoryDao().insert(Category(name = "Основное"))
            database.CategoryDao().insert(Category(name = "Салаты"))
            database.CategoryDao().insert(Category(name = "Десерты"))
            database.CategoryDao().insert(Category(name = "Напитки"))

            for (ingredient in defaultIngredients){
                database.ingredientDao().insert(Ingredient(name = ingredient))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentView)
        return navController.navigateUp()
    }

    private fun navigationActive(){
        val navController = findNavController(R.id.fragmentView)

        val conf = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.ingredientsFragment,
                R.id.categoriesFragment
            )
        )

        setupActionBarWithNavController(navController, conf)
        binding.navController.setupWithNavController(navController)
    }

}