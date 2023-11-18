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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.databinding.FragmentRecipeReadBinding
import com.example.bookofrecipes.rcAdapters.ChoseIngredientRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecipeReadFragment : Fragment(), ChoseIngredientRVAdapter.Listener {

    lateinit var binding: FragmentRecipeReadBinding

    lateinit var database: AppDatabase


    private var recipeID: Long = 0
    private var recipeName: String? = ""
    private var recipeText: String? = ""
    private var recipeCategory: Long = 0
    private var categoryName: String? = ""
    private var ingredientArrayList: MutableList<IngredientCount> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeReadBinding.inflate(inflater)

        binding.ingrRecyclerView.layoutManager = LinearLayoutManager(context)

        val app = requireContext().applicationContext as Application
        database = app.database

        init()

        binding.editButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong("recipeId", recipeID)
            bundle.putString("recipeName", recipeName)
            bundle.putString("recipeText", recipeText)
            bundle.putLong ("recipeCategoryId", recipeCategory)
            bundle.putString ("recipeCategoryName", categoryName)

            setFragmentResult("editRecipe",bundle)
            it.findNavController().navigate(R.id.editRecipeFragment)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(false)
        CoroutineScope(Dispatchers.IO).launch {
            val category = database.CategoryDao().getCategoryById(recipeCategory)

            ingredientArrayList.clear()
            val ingredientsList =
                database.recipesAndIngredientsDao().getIngredientsForRecipe(recipeID)

            for (ingredient in ingredientsList){
                val ingredientName =
                    database.ingredientDao().getIngredientById(ingredient.ingredientId).name
                val ingredientCount = IngredientCount(id = ingredient.ingredientId,
                    name = ingredientName,
                    many = ingredient.howMany)
                ingredientArrayList.add(ingredientCount)
            }
            categoryName = category?.name

            withContext(Dispatchers.Main){
                binding.recipeTypeTV.text = category?.name
                binding.ingrRecyclerView.adapter = ChoseIngredientRVAdapter(this@RecipeReadFragment ,ingredientArrayList, false)
                binding.recipeNameTV.text = recipeName
                binding.recipeTextTV.text = recipeText
            }
        }
    }

    private fun init(){
        setFragmentResultListener("openRecipeRead"){ key, bundle ->
            recipeID = bundle.getLong("recipeId",0)
            recipeName = bundle.getString("recipeName")
            recipeText = bundle.getString("recipeText")
            recipeCategory = bundle.getLong("recipeCategory")
        }
    }

    override fun onDeleteChosenIngredient(ingredientCount: IngredientCount) {

    }

}