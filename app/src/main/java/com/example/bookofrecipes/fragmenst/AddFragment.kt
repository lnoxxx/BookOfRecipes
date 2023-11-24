package com.example.bookofrecipes.fragmenst

import android.animation.LayoutTransition
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
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

class AddFragment : Fragment(), ChoseCategoryRecyclerViewAdapter.Listener,
    ChoseIngredientRVAdapter.Listener {
    private lateinit var bindingAdd: FragmentAddBinding

    private var chosenId: Int = 1
    private var chosenCategoryName = "Без категории"
    private var ingredientCountList :MutableList<IngredientCount> = mutableListOf()

    lateinit var adapter:ChoseCategoryRecyclerViewAdapter
    private lateinit var ingredientAdapter:ChoseIngredientRVAdapter

    private var menuOpen = false

    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //binding
        bindingAdd = FragmentAddBinding.inflate(inflater)

        //database
        val app = requireContext().applicationContext as Application
        database = app.database

        //ingredientList
        val viewModel: SharedViewModel by activityViewModels()
        if (!viewModel.ingredientCountList.isInitialized){
            viewModel.ingredientCountList.value = mutableListOf()
        } else{
            ingredientCountList = viewModel.ingredientCountList.value ?: mutableListOf()
        }

        //ingredientRecyclerView
        bindingAdd.ingredRv.layoutManager = LinearLayoutManager(context)
        ingredientAdapter = ChoseIngredientRVAdapter(this, ingredientCountList)
        bindingAdd.ingredRv.adapter = ingredientAdapter

        //categoryRecyclerView
        initChoseCategoryRV()

        //onClickListeners
        bindingAdd.categoryCardView.setOnClickListener {
            changeMenuView()
        }
        bindingAdd.saveButton.setOnClickListener {
            saveRecipe()
        }
        bindingAdd.ingredientCV.setOnClickListener{
            it.findNavController().navigate(R.id.action_addFragment_to_searchIngredientFragment)
        }

        //defaultCategoryText
        bindingAdd.chosenCategoryTV.text = chosenCategoryName

        //animationInit
        animationInit()
        return bindingAdd.root
    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(true)
        if (chosenId == 1){
            bindingAdd.downArrIcon.animate().apply {
                duration = 300
                rotationX(180f)
            }
            bindingAdd.choseCategoryRV.visibility = View.VISIBLE
            menuOpen = true
        }
    }
    override fun onStart() {
        super.onStart()
        bindingAdd.warningCardView.visibility = View.GONE
        bindingAdd.warningLayput.visibility = View.GONE
    }

    private fun animationInit(){
        bindingAdd.categoryCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        bindingAdd.recipeCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        bindingAdd.recipeTextCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        bindingAdd.ingredientCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        bindingAdd.warningCardView.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        bindingAdd.warningLayput.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
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

    private fun changeMenuView(){
        if (menuOpen){
            bindingAdd.choseCategoryRV.visibility = View.GONE
            bindingAdd.downArrIcon.animate().apply {
                duration = 300
                rotationX(0f)
            }
            menuOpen = false
        } else{
            bindingAdd.downArrIcon.animate().apply {
                duration = 300
                rotationX(180f)
            }
            bindingAdd.choseCategoryRV.visibility = View.VISIBLE
            menuOpen = true
        }
    }
    private fun closeWarring(){
        Handler(Looper.getMainLooper()).postDelayed({bindingAdd.warningCardView.visibility = View.GONE
            bindingAdd.warningLayput .visibility = View.GONE}, 7000)
    }
    private fun saveRecipe(){
        val recipeCheckRes = recipeCurrencyCheck()
        if (recipeCheckRes != "recipeCorrect"){
            bindingAdd.warningText.text = recipeCheckRes
            bindingAdd.warningCardView.visibility = View.VISIBLE
            bindingAdd.warningLayput .visibility = View.VISIBLE
            closeWarring()
            return
        }
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
        bindingAdd.chosenCategoryTV.text = chosenCategoryName
        bindingAdd.warningCardView.visibility = View.GONE
        bindingAdd.warningLayput .visibility = View.GONE
    }
    override fun onClick(category: Category) {
        chosenId = category.id.toInt()
        bindingAdd.chosenCategoryTV.text = category.name
        chosenCategoryName = category.name
        changeMenuView()
    }
    override fun onDeleteChosenIngredient(ingredientCount: IngredientCount) {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.ingredientCountList.value?.remove(ingredientCount)
        ingredientCountList.remove(ingredientCount)
        ingredientAdapter.deleteCount(ingredientCount)
    }
    private fun recipeCurrencyCheck(): String{
        val recipeName = bindingAdd.nameRecipeET.text.toString()
        val recipeText = bindingAdd.recipeTextET.text.toString()
        if (recipeName.isEmpty()){
            return "Название рецепта не может быть пустым!"
        }
        if (recipeName.length > 100){
            return "Название рецепта слишком большое!"
        }
        if (recipeText.isEmpty()){
            return "Рецепт блюда не может быть пустым!"
        }
        if (recipeText.length > 10000){
            return "Рецепт слишком большой!"
        }
        if (ingredientCountList.isEmpty()){
            return "Ваш рецепт должен иметь хотя бы 1 ингредиент!"
        }
        if (ingredientCountList.size > 100){
            return "Слишком много ингредиентов!"
        }
        return "recipeCorrect"
    }


}