package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.SharedViewModel
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.dataBase.RecipesAndIngredients
import com.example.bookofrecipes.databinding.FragmentAddBinding
import com.example.bookofrecipes.rcAdapters.ChoseCategoryRecyclerViewAdapter
import com.example.bookofrecipes.rcAdapters.ChoseIngredientRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragment : Fragment(), ChoseCategoryRecyclerViewAdapter.Listener, ChoseIngredientRVAdapter.Listener {

    private lateinit var bindingAdd: FragmentAddBinding

    private var chosenId: Int = 1
    private var chosenCategoryName = "Без категории"
    private var ingredientCountList :MutableList<IngredientCount> = mutableListOf()

    lateinit var adapter:ChoseCategoryRecyclerViewAdapter

    lateinit var ingredientAdapter:ChoseIngredientRVAdapter

    private var menuOpen = false

    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingAdd = FragmentAddBinding.inflate(inflater)
        val app = requireContext().applicationContext as Application
        database = app.database
        initChoseCategoryRV()
        bindingAdd.categoryCardView.setOnClickListener {
            changeMenuView()
        }
        bindingAdd.chosenCategoryTV.text = chosenCategoryName
        bindingAdd.saveButton.setOnClickListener {
            saveRecipe()
        }

        val viewModel: SharedViewModel by activityViewModels()
        if (!viewModel.ingredientCountList.isInitialized){
            viewModel.ingredientCountList.value = mutableListOf()
        } else{
            ingredientCountList = viewModel.ingredientCountList.value ?: mutableListOf()
        }

        bindingAdd.ingredRv.layoutManager = LinearLayoutManager(context)

        ingredientAdapter = ChoseIngredientRVAdapter(this, ingredientCountList)
        bindingAdd.ingredRv.adapter = ingredientAdapter

        return bindingAdd.root
    }

    override fun onResume() {
        super.onResume()
        bindingAdd.ingredientCV.setOnClickListener{
            it.findNavController().navigate(R.id.searchIngredientFragment)
        }
    }

    private fun changeMenuView(){
        if (menuOpen){
            bindingAdd.choseCategoryRV.visibility = View.GONE
            menuOpen = false
        } else{
            bindingAdd.choseCategoryRV.visibility = View.VISIBLE
            menuOpen = true
        }
    }

    private fun initChoseCategoryRV(){
        bindingAdd.choseCategoryRV.layoutManager =
            LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        CoroutineScope(Dispatchers.IO).launch {
            val categoryList = database.CategoryDao().getAllCategories()
            withContext(Dispatchers.Main) {
                adapter = ChoseCategoryRecyclerViewAdapter(this@AddFragment,categoryList)
                bindingAdd.choseCategoryRV.adapter = adapter
            }
        }
    }

    private fun saveRecipe(){
        CoroutineScope(Dispatchers.IO).launch {
            val addedRecipe = Recipe(name = bindingAdd.nameRecipeET.text.toString(),
                type = chosenId.toLong(),
                recipeText = bindingAdd.recipeTextET.text.toString(),
                isFavorite = false
            )
            val id = database.recipeDao().insert(addedRecipe)
            for (ingredient in ingredientCountList){
                val recipeAndIngredient = RecipesAndIngredients(recipeId = id,
                    ingredientId = ingredient.id,
                    howMany = ingredient.many)
                database.recipesAndIngredientsDao().insert(recipeAndIngredient)
            }
            ingredientCountList.clear()
            withContext(Dispatchers.Main){
                bindingAdd.nameRecipeET.text.clear()
                chosenId = 1
                bindingAdd.recipeTextET.text.clear()
                ingredientAdapter.clearRecyclerView()
            }
        }
        chosenCategoryName = "Без категории"
    }

    override fun onClick(category: Category) {
        chosenId = category.id.toInt()
        bindingAdd.chosenCategoryTV.text = category.name
        chosenCategoryName = category.name
        bindingAdd.choseCategoryRV.visibility = View.GONE
        menuOpen = false
    }

    override fun onDeleteChosenIngredient(ingredientCount: IngredientCount) {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.ingredientCountList.value?.remove(ingredientCount)
        ingredientCountList.remove(ingredientCount)
        ingredientAdapter.deleteCount(ingredientCount)
    }

}