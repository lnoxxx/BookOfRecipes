package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
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

    private var categoryId: Long = -1

    private var favorite: Boolean = false

    private var recipeList: MutableList<Recipe> = mutableListOf()

    private lateinit var adapter: RecipeRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryRecipeBinding.inflate(inflater)
        val app = requireContext().applicationContext as Application
        database = app.database

        binding.categoryRecipeRV.alpha = 0f

        binding.categoryRecipeRV.layoutManager = LinearLayoutManager(context)

        setFragmentResultListener("categoryResult"){ _, bundle ->
            categoryId = bundle.getLong("chosenCategoryForView")
            favorite = bundle.getBoolean("favorite?")
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (favorite){
            CoroutineScope(Dispatchers.IO).launch {
                recipeList = database.recipeDao().getFavoriteRecipe()
                withContext(Dispatchers.Main){
                    if (recipeList.isEmpty()){
                        binding.noRecipeTv.visibility = View.VISIBLE
                    } else {
                        binding.noRecipeTv.visibility = View.GONE
                    }
                    adapter = RecipeRecyclerViewAdapter(this@CategoryRecipeFragment,recipeList, false)
                    binding.categoryRecipeRV.adapter = adapter
                    binding.categoryRecipeRV.animate().apply {
                        duration = 120
                        alpha(1f)
                    }
                }
            }
        } else{
            CoroutineScope(Dispatchers.IO).launch {
                recipeList = database.recipeDao().getRecipeInCategory(categoryId)
                withContext(Dispatchers.Main){
                    if (recipeList.isEmpty()){
                        binding.noRecipeTv.visibility = View.VISIBLE
                    } else {
                        binding.noRecipeTv.visibility = View.GONE
                    }
                    adapter = RecipeRecyclerViewAdapter(this@CategoryRecipeFragment,recipeList, false)
                    binding.categoryRecipeRV.adapter = adapter
                    binding.categoryRecipeRV.animate().apply {
                        duration = 120
                        alpha(1f)
                    }
                }
            }
        }
        (activity as MainActivity).setBottomNavigationVisibility(false)
    }


    override fun onClick(recipe: Recipe) {
        val bundle = Bundle()
        bundle.putLong("recipeId",recipe.id)
        bundle.putString("recipeName", recipe.name)
        bundle.putString("recipeText", recipe.recipeText)
        bundle.putLong("recipeCategory", recipe.type)

        setFragmentResult("openRecipeRead",bundle)
        findNavController().navigate(R.id.action_categoryRecipeFragment_to_recipeReadFragment)
    }

    override fun onDelete(recipe: Recipe) {

    }

    override fun onMakeFavorite(recipe: Recipe) {

    }

    override fun onDeleteInFavorite(recipe: Recipe) {

    }


}