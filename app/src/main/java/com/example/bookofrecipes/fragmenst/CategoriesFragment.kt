package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.os.Bundle
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
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.FragmentCategoriesBinding
import com.example.bookofrecipes.rcAdapters.CategoryRcAdapter
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
        //binding
        bindingCategories = FragmentCategoriesBinding.inflate(inflater)
        //database
        val app = requireContext().applicationContext as Application
        database = app.database
        //on click listeners
        bindingCategories.button3.setOnClickListener {
            showInputDialog()
        }
        bindingCategories.favoriteButton.setOnClickListener {
            val categoryBundle = Bundle()
            categoryBundle.putBoolean("favorite?",true)
            setFragmentResult("categoryResult",categoryBundle)
            findNavController().navigate(R.id.action_categoriesFragment_to_categoryRecipeFragment2)
        }
        //recyclerView
        recyclerViewInit()
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

    private fun showDeleteDialog(category: Category) {
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyleDelete)
        val view = layoutInflater.inflate(R.layout.dialog_delete, null)
        val title = view.findViewById<TextView>(R.id.deleteTitle)
        title.text = "Удалить категорию?"
        builder.setView(view)
        builder.setPositiveButton("Удалить",) { dialog, _ ->
            if (category.id.toInt() != 1){
                CoroutineScope(Dispatchers.IO).launch {
                    val recipeInCategoryList = database.recipeDao().getRecipeInCategory(category.id)
                    for (recipe in recipeInCategoryList){
                        val changedRecipe = Recipe(
                            id = recipe.id,
                            name = recipe.name,
                            type = 1,
                            recipeText = recipe.recipeText,
                            isFavorite = recipe.isFavorite,
                            photoId = recipe.photoId
                        )
                        database.recipeDao().updateRecipe(changedRecipe)
                    }
                    database.CategoryDao().deleteCategory(category)
                    withContext(Dispatchers.Main){
                        adapter.deleteCategory(category)
                    }
                }
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        val title = view.findViewById<TextView>(R.id.dialogeTitleTV)
        title.text = "Добавить категорию"
        builder.setView(view)
        builder.setPositiveButton("Добавить") { dialog, _ ->
            if (editText.text.toString().isEmpty()) {
                Toast.makeText(
                    context,
                    "Название категории не может быть пустым!",
                    Toast.LENGTH_LONG
                ).show()
            } else if (editText.text.toString().length > 40) {
                Toast.makeText(context, "Название категории слишком длинное!", Toast.LENGTH_LONG)
                    .show()
            } else {
                val inputText = editText.text.toString()
                val addedCategory = Category(name = inputText)
                CoroutineScope(Dispatchers.IO).launch {
                    val id = database.CategoryDao().insert(addedCategory)
                    withContext(Dispatchers.Main) {
                        val newCategory = Category(id = id, name = addedCategory.name)
                        adapter.addCategory(newCategory)
                    }
                }
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onClick(category: Category) {
        val categoryBundle = Bundle()
        categoryBundle.putLong("chosenCategoryForView",category.id)
        setFragmentResult("categoryResult",categoryBundle)
        findNavController().navigate(R.id.action_categoriesFragment_to_categoryRecipeFragment2)
    }

    override fun onDelete(category: Category) {
        showDeleteDialog(category)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(true)
    }
}