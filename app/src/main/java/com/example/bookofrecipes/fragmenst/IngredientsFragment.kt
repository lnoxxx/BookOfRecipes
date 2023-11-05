package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Ingredient
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

        recyclerViewInit()

        bindingIngredients.addIngredientButton.setOnClickListener{
            showInputDialog()
        }

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
            }
        }
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        builder.setView(view)
        builder.setPositiveButton(R.string.dialoge_add_button, DialogInterface.OnClickListener { dialog, _ ->
            val inputText = editText.text.toString()
            val addIngredient = Ingredient(name = inputText)
            CoroutineScope(Dispatchers.IO).launch {
                database.ingredientDao().insert(addIngredient)
            }
            adapter.addIngredient(addIngredient)
            dialog.dismiss()
        })
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDeleteIngredient(ingredient: Ingredient) {
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
    }


}