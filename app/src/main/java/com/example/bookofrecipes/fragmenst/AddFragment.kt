package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.dataBase.RecipesAndIngredients
import com.example.bookofrecipes.databinding.FragmentAddBinding
import com.example.bookofrecipes.rcAdapters.CategoryRcAdapter
import com.example.bookofrecipes.rcAdapters.ChoseCategoryRecyclerViewAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragment : Fragment(), ChoseCategoryRecyclerViewAdapter.Listener {

    private lateinit var bindingAdd: FragmentAddBinding

    var chosenId: Int = 1
    var chosenCategoryName = "Без категории"

    lateinit var adapter:ChoseCategoryRecyclerViewAdapter

    var menuOpen = false

    var ingredientCountList = ArrayList<IngredientCount>()

    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingAdd = FragmentAddBinding.inflate(inflater)

        val app = requireContext().applicationContext as Application
        database = app.database

        bindingAdd.choseCategoryRV.layoutManager =
            LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        CoroutineScope(Dispatchers.IO).launch {
            val categoryList = database.CategoryDao().getAllCategories()
            withContext(Dispatchers.Main) {
                adapter = ChoseCategoryRecyclerViewAdapter(this@AddFragment,categoryList)
                bindingAdd.choseCategoryRV.adapter = adapter
            }
        }


        bindingAdd.ingredientCV.setOnClickListener{
            val bundleList = Bundle()
            bundleList.putSerializable("ingView", ingredientCountList)
            setFragmentResult("ingListOpen", bundleList)
            it.findNavController().navigate(R.id.choseIngredientFragment)
        }


        bindingAdd.categoryCardView.setOnClickListener {
            if (menuOpen){
                bindingAdd.choseCategoryRV.visibility = View.GONE
                menuOpen = false
            } else{
                bindingAdd.choseCategoryRV.visibility = View.VISIBLE
                menuOpen = true
            }
        }

        bindingAdd.chosenCategoryTV.text = chosenCategoryName

        setFragmentResultListener("ingResult"){ key, bundle ->
            ingredientCountList = bundle.getSerializable("ingList") as ArrayList<IngredientCount>
        }


        bindingAdd.saveButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val addedRecipe = Recipe(name = bindingAdd.nameRecipeET.text.toString(),
                    type = chosenId,
                    recipeText = bindingAdd.recipeTextET.text.toString(),
                    isFavorite = false
                )
                val id = database.recipeDao().insert(addedRecipe)
                for (ingredient in ingredientCountList){
                    val recipeAndIngredient = RecipesAndIngredients(recipeId = id,
                        ingredientId = ingredient.id.toLong(),
                        howMany = ingredient.many)
                    database.recipesAndIngredientsDao().insert(recipeAndIngredient)
                }
                ingredientCountList.clear()
            }
            bindingAdd.nameRecipeET.text.clear()
            chosenId = 1
            chosenCategoryName = "Без категории"
            bindingAdd.recipeTextET.text.clear()
        }

        return bindingAdd.root
    }

    override fun onClick(category: Category) {
        chosenId = category.id.toInt()
        bindingAdd.chosenCategoryTV.text = category.name
        chosenCategoryName = category.name
        adapter.changeChose(category.id.toInt())
        bindingAdd.choseCategoryRV.visibility = View.GONE
        menuOpen = false

    }
}