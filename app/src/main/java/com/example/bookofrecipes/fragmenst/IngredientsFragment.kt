package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.FragmentIngredientsBinding
import com.example.bookofrecipes.rcAdapters.IngredientRcAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngredientsFragment : Fragment() {

    private lateinit var bindingIngredients: FragmentIngredientsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingIngredients = FragmentIngredientsBinding.inflate(inflater)


        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "my_database"
        ).build()

        bindingIngredients.rcIngredients.layoutManager = LinearLayoutManager(context)
        bindingIngredients.rcIngredients.clipToPadding = false
        bindingIngredients.rcIngredients.setPadding(0, 0, 0, 200)


        CoroutineScope(Dispatchers.IO).launch {
            val ingredientList = database.ingredientDao().getAllIngredients()
            withContext(Dispatchers.Main) {
                val adapter = IngredientRcAdapter(ingredientList)
                bindingIngredients.rcIngredients.adapter = adapter
            }
        }

        bindingIngredients.button2.setOnClickListener{
            showInputDialog()
        }

        bindingIngredients.rcIngredients.animate().apply {
            duration = 300
            alpha(1f)
        }

        return bindingIngredients.root
    }


    private fun showInputDialog() {

        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)

        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.editTextText3)
        builder.setView(view)

        builder.setNegativeButton("Отмена", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })

        builder.setPositiveButton("Добавить", DialogInterface.OnClickListener { dialog, _ ->
            val inputText = editText.text.toString()

            val database = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java,
                "my_database"
            ).build()

            val addedIngredient = Ingredient(name = inputText)

            CoroutineScope(Dispatchers.IO).launch {
                database.ingredientDao().insert(addedIngredient)
                val ingredientList = database.ingredientDao().getAllIngredients()
                withContext(Dispatchers.Main) {
                    val adapter = IngredientRcAdapter(ingredientList)
                    bindingIngredients.rcIngredients.adapter = adapter
                }
            }

            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }


}