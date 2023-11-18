package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.FragmentCategoriesBinding
import com.example.bookofrecipes.rcAdapters.CategoryRcAdapter
import com.example.bookofrecipes.rcAdapters.IngredientRcAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment(), CategoryRcAdapter.Listener {

    private lateinit var bindingCategories: FragmentCategoriesBinding

    lateinit var adapter: CategoryRcAdapter

    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingCategories = FragmentCategoriesBinding.inflate(inflater)

        val app = requireContext().applicationContext as Application
        database = app.database

        bindingCategories.button3.setOnClickListener {
            showInputDialog()
        }

        bindingCategories.favoriteButton.setOnClickListener {
            val categoryBundle = Bundle()
            categoryBundle.putBoolean("favorite?",true)
            setFragmentResult("categoryResult",categoryBundle)

            findNavController().navigate(R.id.categoryRecipeFragment)
        }

        return bindingCategories.root
    }

    private fun recyclerViewInit(){
        bindingCategories.rcCategory.layoutManager = GridLayoutManager(context,2)
        CoroutineScope(Dispatchers.IO).launch {
            val categoryList = database.CategoryDao().getAllCategories()
            withContext(Dispatchers.Main) {
                adapter = CategoryRcAdapter(this@CategoriesFragment,categoryList)
                bindingCategories.rcCategory.adapter = adapter
            }
        }
    }

    private fun showInputDialog() {

        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)

        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        val title = view.findViewById<TextView>(R.id.dialogeTitleTV)
        title.text = "Добавить категорию"
        builder.setView(view)

        builder.setPositiveButton("Добавить", DialogInterface.OnClickListener { dialog, _ ->
            if (editText.text.toString().isEmpty()){
                Toast.makeText(context,"Название категории не может быть пустым!", Toast.LENGTH_LONG).show()
            } else if (editText.text.toString().length > 40){
                Toast.makeText(context,"Название категории слишком длинное!", Toast.LENGTH_LONG).show()
            }else {
                val inputText = editText.text.toString()
                val addedCategory = Category(name = inputText)
                CoroutineScope(Dispatchers.IO).launch {
                    database.CategoryDao().insert(addedCategory)
                    withContext(Dispatchers.Main) {
                        adapter.addCategory(addedCategory)
                    }
                }
                dialog.dismiss()
            }
        })

        val dialog = builder.create()
        dialog.show()
    }

    override fun onClick(category: Category) {
        val categoryBundle = Bundle()
        categoryBundle.putLong("chosenCategoryForView",category.id)
        setFragmentResult("categoryResult",categoryBundle)

        findNavController().navigate(R.id.categoryRecipeFragment)
    }

    override fun onDelete(category: Category) {
        if (category.id.toInt() == 1){
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val recipeInCategoryList = database.recipeDao().getRecipeInCategory(category.id)
            for (recipe in recipeInCategoryList){
                val changedRecipe = Recipe(
                    id = recipe.id,
                    name = recipe.name,
                    type = 1,
                    recipeText = recipe.recipeText,
                    isFavorite = recipe.isFavorite)
                database.recipeDao().updateRecipe(changedRecipe)
            }
            database.CategoryDao().deleteCategory(category)
            withContext(Dispatchers.Main){
                adapter.deleteCategory(category)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(true)
        recyclerViewInit()
    }
}