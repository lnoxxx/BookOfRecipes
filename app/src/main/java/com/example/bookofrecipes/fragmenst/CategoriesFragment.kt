package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.FragmentCategoriesBinding
import com.example.bookofrecipes.rcAdapters.CategoryRcAdapter
import com.example.bookofrecipes.rcAdapters.IngredientRcAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment() {

    private lateinit var bindingCategories: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingCategories = FragmentCategoriesBinding.inflate(inflater)

        val database = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java,
            "my_database"
        ).build()

        bindingCategories.rcCategory.layoutManager = GridLayoutManager(context,2)
        CoroutineScope(Dispatchers.IO).launch {
            val categoryList = database.CategoryDao().getAllCategories()
            withContext(Dispatchers.Main) {
                val adapter = CategoryRcAdapter(categoryList)
                bindingCategories.rcCategory.adapter = adapter
            }
        }

        bindingCategories.button3.setOnClickListener {
            showInputDialog()
        }

        return bindingCategories.root
    }

    private fun showInputDialog() {

        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)

        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        val title = view.findViewById<TextView>(R.id.dialogeTitleTV)
        title.text = "Добавить категорию"
        builder.setView(view)


        builder.setPositiveButton("Добавить", DialogInterface.OnClickListener { dialog, _ ->
            val inputText = editText.text.toString()

            val database = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java,
                "my_database"
            ).build()

            val addedCategory = Category(name = inputText)

            CoroutineScope(Dispatchers.IO).launch {
                database.CategoryDao().insert(addedCategory)
                val categoryList = database.CategoryDao().getAllCategories()
                withContext(Dispatchers.Main) {
                    val adapter = CategoryRcAdapter(categoryList)
                    bindingCategories.rcCategory.adapter = adapter
                }
            }

            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }
}