package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.FragmentCategoryRecipeBinding
import com.example.bookofrecipes.rcAdapters.RecipeRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CategoryRecipeFragment : Fragment(), RecipeRecyclerViewAdapter.Listener {

    lateinit var binding: FragmentCategoryRecipeBinding

    lateinit var database: AppDatabase

    var categoryId: Long = -1

    private var recipeList: MutableList<Recipe> = mutableListOf()

    private lateinit var adapter: RecipeRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryRecipeBinding.inflate(inflater)
        val app = requireContext().applicationContext as Application
        database = app.database

        binding.categoryRecipeRV.layoutManager = LinearLayoutManager(context)

        setFragmentResultListener("categoryResult"){requestKey, bundle ->
            categoryId = bundle.getLong("chosenCategoryForView")
            CoroutineScope(Dispatchers.IO).launch {
                recipeList = database.recipeDao().getRecipeInCategory(categoryId)
                withContext(Dispatchers.Main){
                    adapter = RecipeRecyclerViewAdapter(this@CategoryRecipeFragment,recipeList)
                    binding.categoryRecipeRV.adapter = adapter
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        if (categoryId.toInt() == -1){
            fragmentManager?.popBackStack()
        }
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            recipeList = database.recipeDao().getRecipeInCategory(categoryId)
            withContext(Dispatchers.Main){
                adapter = RecipeRecyclerViewAdapter(this@CategoryRecipeFragment,recipeList)
                binding.categoryRecipeRV.adapter = adapter
            }
        }
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