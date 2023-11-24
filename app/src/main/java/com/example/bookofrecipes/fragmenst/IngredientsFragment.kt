package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.dataBase.Recipe
import com.example.bookofrecipes.databinding.FragmentIngredientsBinding
import com.example.bookofrecipes.rcAdapters.IngredientRcAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientsFragment : Fragment(), IngredientRcAdapter.Listener {
    private lateinit var bindingIngredients: FragmentIngredientsBinding

    lateinit var database: AppDatabase

    lateinit var adapter: IngredientRcAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingIngredients = FragmentIngredientsBinding.inflate(inflater)
        val app = requireContext().applicationContext as Application
        database = app.database
        bindingIngredients.addIngredientButton.setOnClickListener{
            showInputDialog()
        }
        recyclerViewInit()
        return bindingIngredients.root
    }

    private fun recyclerViewInit(){
        bindingIngredients.rcIngredients.layoutManager = LinearLayoutManager(context)
        bindingIngredients.rcIngredients.clipToPadding = false
        bindingIngredients.rcIngredients.setPadding(0, 0, 0, 200)
        CoroutineScope(Dispatchers.IO).launch {
            val ingredientList = database.ingredientDao().getAllIngredients()
            withContext(Dispatchers.Main) {
                adapter = IngredientRcAdapter(this@IngredientsFragment, ingredientList)
                bindingIngredients.rcIngredients.adapter = adapter
                searchInit()
            }
        }
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        builder.setView(view)
        builder.setPositiveButton(R.string.dialoge_add_button) { dialog, _ ->
            if (editText.text.toString().isEmpty()) {
                Toast.makeText(
                    context,
                    "Название ингредиента не может быть пустым!",
                    Toast.LENGTH_LONG
                ).show()
            } else if (editText.text.toString().length > 40) {
                Toast.makeText(context, "Название ингредиента слишком длинное!", Toast.LENGTH_LONG)
                    .show()
            } else {
                val inputText = editText.text.toString()
                val addIngredient = Ingredient(name = inputText)
                CoroutineScope(Dispatchers.IO).launch {
                    val id = database.ingredientDao().insert(addIngredient)
                    val newIngredient = Ingredient(id = id, name = addIngredient.name)
                    withContext(Dispatchers.Main){
                        adapter.addIngredient(newIngredient)
                    }
                }
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteDialog(ingredient: Ingredient) {
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyleDelete)
        val view = layoutInflater.inflate(R.layout.dialog_delete, null)
        val title = view.findViewById<TextView>(R.id.deleteTitle)
        title.text = "Удалить ингредиент?"
        builder.setView(view)
        builder.setPositiveButton("Удалить",) { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                val ingredientInRecipeList =
                    database.recipesAndIngredientsDao().getWhereIngredientId(ingredient.id)
                for (item in ingredientInRecipeList){
                    database.recipesAndIngredientsDao().deleteRecipeAndIngredient(item)
                }
                database.ingredientDao().deleteIngredient(ingredient)
                withContext(Dispatchers.Main){
                    adapter.deleteIngredient(ingredient)
                }
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDeleteIngredient(ingredient: Ingredient) {
        showDeleteDialog(ingredient)
    }

    private fun searchInit(){
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val ingredientList = database.ingredientDao().searchEntities("%${text}%")
                    withContext(Dispatchers.Main){
                        adapter.clearSearch(ingredientList)
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        bindingIngredients.editTextText3.addTextChangedListener(textWatcher)
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(true)
        bindingIngredients.editTextText3.text.clear()
    }
}