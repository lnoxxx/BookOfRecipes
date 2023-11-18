package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.FragmentRecipesBinding
import com.example.bookofrecipes.rcAdapters.RecipeRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecipesFragment : Fragment(), RecipeRecyclerViewAdapter.Listener {

    private lateinit var bindingRecipes: FragmentRecipesBinding

    lateinit var database: AppDatabase

    lateinit var adapter: RecipeRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingRecipes = FragmentRecipesBinding.inflate(inflater)
        adapter = RecipeRecyclerViewAdapter(this, mutableListOf())

        val app = requireContext().applicationContext as Application
        database = app.database

        return bindingRecipes.root
    }

    private fun recyclerViewInit(){
        bindingRecipes.recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        CoroutineScope(Dispatchers.IO).launch {
            val recipeArray = database.recipeDao().getAllRecipes()
            withContext(Dispatchers.Main){
                adapter = RecipeRecyclerViewAdapter(this@RecipesFragment,recipeArray)
                bindingRecipes.recipeRecyclerView.adapter = adapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(true)
        recyclerViewInit()
        searchInit()
        bindingRecipes.searchRecipeEditText.text.clear()
    }

    private fun searchInit(){
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val recipeList = database.recipeDao().searchRecipeByName("%${text}%")
                    withContext(Dispatchers.Main){
                        adapter.clearSearch(recipeList)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }

        bindingRecipes.searchRecipeEditText.addTextChangedListener(textWatcher)
    }

    override fun onClick(recipe: Recipe) {
        val bundle = Bundle()
        bundle.putLong("recipeId",recipe.id)
        bundle.putString("recipeName", recipe.name)
        bundle.putString("recipeText", recipe.recipeText)
        bundle.putLong("recipeCategory", recipe.type.toLong())

        setFragmentResult("openRecipeRead",bundle)
        findNavController().navigate(R.id.recipeReadFragment)
    }

    override fun onDelete(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val recipeIngredientList = database.recipesAndIngredientsDao().getIngredientsForRecipe(recipe.id)
            for (recipeIngredient in recipeIngredientList){
                database.recipesAndIngredientsDao().deleteRecipeAndIngredient(recipeIngredient)
            }
            database.recipeDao().deleteRecipe(recipe)
            withContext(Dispatchers.Main){
                adapter.deleteRecipe(recipe)
            }
        }
    }

    override fun onMakeFavorite(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val newRecipe = Recipe(
                id = recipe.id,
                name = recipe.name,
                type = recipe.type,
                recipeText = recipe.recipeText,
                isFavorite = true
            )
            database.recipeDao().updateRecipe(newRecipe)
        }
    }

    override fun onDeleteInFavorite(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val newRecipe = Recipe(
                id = recipe.id,
                name = recipe.name,
                type = recipe.type,
                recipeText = recipe.recipeText,
                isFavorite = false
            )
            database.recipeDao().updateRecipe(newRecipe)
        }
    }



}