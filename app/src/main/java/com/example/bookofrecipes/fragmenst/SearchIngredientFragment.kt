package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Category
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.FragmentSearchIngredientBinding
import com.example.bookofrecipes.rcAdapters.CategoryRcAdapter
import com.example.bookofrecipes.rcAdapters.SearchIngredientRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchIngredientFragment : Fragment(), SearchIngredientRVAdapter.Listener {

    lateinit var binding: FragmentSearchIngredientBinding

    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchIngredientBinding.inflate(inflater)

        binding.rvIngredientSearch.layoutManager = LinearLayoutManager(context)

        val app = requireContext().applicationContext as Application
        database = app.database

        CoroutineScope(Dispatchers.IO).launch {
            val ingredientList = database.ingredientDao().getAllIngredients()
            withContext(Dispatchers.Main){
                val adapter = SearchIngredientRVAdapter(this@SearchIngredientFragment,ingredientList)
                binding.rvIngredientSearch.adapter = adapter
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val ingredientList = database.ingredientDao().searchEntities("%${text}%")
                    withContext(Dispatchers.Main){
                        val adapter = SearchIngredientRVAdapter(
                            this@SearchIngredientFragment,ingredientList)
                        binding.rvIngredientSearch.adapter = adapter
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.editTextText2.addTextChangedListener(textWatcher)

        return binding.root
    }

    override fun onClick(ingredient: Ingredient) {
        showInputDialog(ingredient)
    }

    private fun showInputDialog(ingredient: Ingredient) {

        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)

        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.editTextText3)
        val title = view.findViewById<TextView>(R.id.textView)
        title.text = "Количество ингредиента"
        builder.setView(view)

        builder.setPositiveButton("Добавить", DialogInterface.OnClickListener { dialog, _ ->
            val inputText = editText.text.toString()

            val bundle = Bundle().apply {
                putInt("ingId", ingredient.id.toInt())
                putString("ingName", ingredient.name)
                putString("ingCount",inputText)
            }

            setFragmentResult("ingSelect", bundle)
            fragmentManager?.popBackStack()

            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

}