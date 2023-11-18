package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.SharedViewModel
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.dataBase.RecipesAndIngredients
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.databinding.FragmentAddBinding
import com.example.bookofrecipes.databinding.FragmentEditRecipeBinding
import com.example.bookofrecipes.rcAdapters.ChoseCategoryRecyclerViewAdapter
import com.example.bookofrecipes.rcAdapters.ChoseIngredientRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditRecipeFragment : Fragment(), ChoseCategoryRecyclerViewAdapter.Listener, ChoseIngredientRVAdapter.Listener  {

    private lateinit var binding: FragmentEditRecipeBinding

    private var chosenId: Long = 1
    private var chosenCategoryName: String? = "Без категории"
    private var ingredientCountList :MutableList<IngredientCount> = mutableListOf()

    lateinit var adapter: ChoseCategoryRecyclerViewAdapter

    lateinit var ingredientAdapter: ChoseIngredientRVAdapter

    private var menuOpen = false

    lateinit var database: AppDatabase

    var recipeID: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditRecipeBinding.inflate(inflater)
        val app = requireContext().applicationContext as Application
        database = app.database
        initChoseCategoryRV()
        binding.categoryCardView.setOnClickListener {
            changeMenuView()
        }
        binding.chosenCategoryTV.text = chosenCategoryName
        binding.saveButton.setOnClickListener {
            saveRecipe()
        }

        val viewModel: SharedViewModel by activityViewModels()
        if (!viewModel.ingredientEditList.isInitialized){
            viewModel.ingredientEditList.value = mutableListOf()
        } else{
            ingredientCountList = viewModel.ingredientEditList.value ?: mutableListOf()
        }

        binding.ingredRv.layoutManager = LinearLayoutManager(context)

        ingredientAdapter = ChoseIngredientRVAdapter(this, ingredientCountList)
        binding.ingredRv.adapter = ingredientAdapter

        setFragmentResultListener("editRecipe"){ key, bundle ->
            recipeID = bundle.getLong("recipeId")
            binding.nameRecipeET.setText(bundle.getString("recipeName"))
            chosenId = bundle.getLong("recipeCategoryId")
            chosenCategoryName = bundle.getString("recipeCategoryName")
            binding.chosenCategoryTV.text = chosenCategoryName
            binding.recipeTextET.setText(bundle.getString("recipeText"))

            ingredientCountList.clear()
            CoroutineScope(Dispatchers.IO).launch {
                val ingList = database.recipesAndIngredientsDao().getIngredientsForRecipe(recipeID)
                for (ing in ingList){
                    val recipeName = database.ingredientDao().getIngredientById(ing.ingredientId).name
                    val ingCount = IngredientCount(id = ing.ingredientId,
                        many = ing.howMany,
                        name = recipeName
                    )
                    ingredientCountList.add(ingCount)
                }
                withContext(Dispatchers.Main){
                    viewModel.ingredientEditList.value = ingredientCountList
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.ingredientCV.setOnClickListener{
            it.findNavController().navigate(R.id.ingredientEditFragment)
        }
        (activity as MainActivity).setBottomNavigationVisibility(false)
    }

    private fun changeMenuView(){
        if (menuOpen){
            binding.choseCategoryRV.visibility = View.GONE
            menuOpen = false
        } else{
            binding.choseCategoryRV.visibility = View.VISIBLE
            menuOpen = true
        }
    }

    private fun initChoseCategoryRV(){
        binding.choseCategoryRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

        CoroutineScope(Dispatchers.IO).launch {
            val categoryList = database.CategoryDao().getAllCategories()
            withContext(Dispatchers.Main) {
                adapter = ChoseCategoryRecyclerViewAdapter(this@EditRecipeFragment,categoryList)
                binding.choseCategoryRV.adapter = adapter
            }
        }
    }

    private fun saveRecipe(){
        val recipeCheckRes = recipeCurrencyCheck()
        if (recipeCheckRes != "recipeCorrect"){
            binding.warningText.text = recipeCheckRes
            binding.warningCardView.visibility = View.VISIBLE
            closeWarring()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val changedRecipe = Recipe(
                id = recipeID,
                name = binding.nameRecipeET.text.toString(),
                type = chosenId,
                recipeText = binding.recipeTextET.text.toString(),
                isFavorite = false
            )
            database.recipeDao().updateRecipe(changedRecipe)
            val oldIngredients = database.recipesAndIngredientsDao().getIngredientsForRecipe(recipeID)
            for (oldIngredient in oldIngredients){
                database.recipesAndIngredientsDao().deleteRecipeAndIngredient(oldIngredient)
            }
            for (ingredient in ingredientCountList){
                val recipeAndIngredient = RecipesAndIngredients(recipeId = recipeID,
                    ingredientId = ingredient.id,
                    howMany = ingredient.many)
                database.recipesAndIngredientsDao().insert(recipeAndIngredient)
            }
            ingredientCountList.clear()
            withContext(Dispatchers.Main){
                binding.nameRecipeET.text.clear()
                chosenId = 1
                binding.recipeTextET.text.clear()
                ingredientAdapter.clearRecyclerView()

                val bundle = Bundle()
                bundle.putLong("recipeId",changedRecipe.id)
                bundle.putString("recipeName", changedRecipe.name)
                bundle.putString("recipeText", changedRecipe.recipeText)
                bundle.putLong("recipeCategory", changedRecipe.type.toLong())
                setFragmentResult("openRecipeRead",bundle)

                findNavController().popBackStack()
            }
        }
        chosenCategoryName = "Без категории"
    }

    override fun onClick(category: Category) {
        chosenId = category.id
        binding.chosenCategoryTV.text = category.name
        chosenCategoryName = category.name
        binding.choseCategoryRV.visibility = View.GONE
        menuOpen = false
    }

    override fun onDeleteChosenIngredient(ingredientCount: IngredientCount) {
        val viewModel: SharedViewModel by activityViewModels()
        viewModel.ingredientEditList.value?.remove(ingredientCount)
        ingredientCountList.remove(ingredientCount)
        ingredientAdapter.deleteCount(ingredientCount)
    }

    private fun recipeCurrencyCheck(): String{
        val recipeName = binding.nameRecipeET.text.toString()
        val recipeText = binding.recipeTextET.text.toString()
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

    override fun onStart() {
        super.onStart()
        binding.warningCardView.visibility = View.GONE
    }

    private fun closeWarring(){
        Handler(Looper.getMainLooper()).postDelayed({binding.warningCardView.visibility = View.GONE}, 7000)
    }

}