package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingRecipes = FragmentRecipesBinding.inflate(inflater)

        val app = requireContext().applicationContext as Application
        database = app.database

        bindingRecipes.recipeRecyclerView.layoutManager = LinearLayoutManager(context)

        CoroutineScope(Dispatchers.IO).launch {
            val recipeArray = database.recipeDao().getAllRecipes()
            withContext(Dispatchers.Main){
                val adapter = RecipeRecyclerViewAdapter(this@RecipesFragment,recipeArray)
                bindingRecipes.recipeRecyclerView.adapter = adapter
            }
        }

        return bindingRecipes.root
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

}