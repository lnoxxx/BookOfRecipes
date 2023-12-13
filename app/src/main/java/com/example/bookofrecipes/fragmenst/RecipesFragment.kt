package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Ingredient
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
        //binding
        bindingRecipes = FragmentRecipesBinding.inflate(inflater)
        //database
        val app = requireContext().applicationContext as Application
        database = app.database
        //init
        recyclerViewInit()
        bindingRecipes.openAddRecipeFragmentButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_addFragment)
        }
        return bindingRecipes.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(true)
        bindingRecipes.searchRecipeEditText.text.clear()
    }

    private fun recyclerViewInit(){
        bindingRecipes.recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        CoroutineScope(Dispatchers.IO).launch {
            val recipeArray = database.recipeDao().getAllRecipes()
            withContext(Dispatchers.Main){
                if (recipeArray.isEmpty()){
                    bindingRecipes.noRecipeTV.visibility = View.VISIBLE
                } else {
                    bindingRecipes.noRecipeTV.visibility = View.GONE
                }
                adapter = RecipeRecyclerViewAdapter(this@RecipesFragment,recipeArray, true, requireContext()
                )
                bindingRecipes.recipeRecyclerView.adapter = adapter
                searchInit()
            }
        }
    }

    private fun showDeleteDialog(recipe: Recipe) {
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyleDelete)
        val view = layoutInflater.inflate(R.layout.dialog_delete, null)
        val title = view.findViewById<TextView>(R.id.deleteTitle)
        title.text = "Удалить рецепт?"
        builder.setView(view)
        builder.setPositiveButton("Удалить",) { dialog, _ ->
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
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
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
        bundle.putLong("recipeCategory", recipe.type)
        bundle.putString("recipePhoto", recipe.photoId)
        setFragmentResult("openRecipeRead",bundle)
        findNavController().navigate(R.id.action_recipesFragment_to_recipeReadFragment)
    }

    override fun onDelete(recipe: Recipe) {
        showDeleteDialog(recipe)
    }

    override fun onMakeFavorite(recipe: Recipe) {
        CoroutineScope(Dispatchers.IO).launch {
            val newRecipe = Recipe(
                id = recipe.id,
                name = recipe.name,
                type = recipe.type,
                recipeText = recipe.recipeText,
                isFavorite = true,
                photoId = recipe.photoId
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
                isFavorite = false,
                photoId = recipe.photoId
            )
            database.recipeDao().updateRecipe(newRecipe)
        }
    }

}