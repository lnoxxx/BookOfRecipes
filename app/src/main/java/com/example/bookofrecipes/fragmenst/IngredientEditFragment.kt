package com.example.bookofrecipes.fragmenst

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.MainActivity
import com.example.bookofrecipes.R
import com.example.bookofrecipes.SharedViewModel
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.databinding.FragmentSearchIngredientBinding
import com.example.bookofrecipes.rcAdapters.SearchIngredientRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class IngredientEditFragment : Fragment(), SearchIngredientRVAdapter.Listener {
    lateinit var binding: FragmentSearchIngredientBinding

    lateinit var database: AppDatabase

    lateinit var adapter: SearchIngredientRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchIngredientBinding.inflate(inflater)
        val app = requireContext().applicationContext as Application
        database = app.database

        recyclerViewInit()

        searchInit()

        binding.searchAddIngredientButt.setOnClickListener {
            showInputDialogAddIngredient()
        }

        return binding.root
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

        binding.editTextText2.addTextChangedListener(textWatcher)
    }

    private fun recyclerViewInit(){
        binding.rvIngredientSearch.layoutManager = LinearLayoutManager(context)
        CoroutineScope(Dispatchers.IO).launch {
            val ingredientList = database.ingredientDao().getAllIngredients()
            withContext(Dispatchers.Main){
                adapter = SearchIngredientRVAdapter(this@IngredientEditFragment,ingredientList)
                binding.rvIngredientSearch.adapter = adapter
            }
        }
    }

    override fun onClick(ingredient: Ingredient) {
        showInputDialog(ingredient)
    }

    private fun showInputDialog(ingredient: Ingredient) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialogStyle)

        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        val title = view.findViewById<TextView>(R.id.dialogeTitleTV)
        title.text = "Количество ингредиента"
        builder.setView(view)

        builder.setPositiveButton(R.string.dialoge_add_button, DialogInterface.OnClickListener { dialog, _ ->
            val inputText = editText.text.toString()

            val ingredientCount = IngredientCount(
                id = ingredient.id,
                many = inputText,
                name = ingredient.name)

            val viewModel: SharedViewModel by activityViewModels()
            viewModel.ingredientEditList.value?.add(ingredientCount)

            fragmentManager?.popBackStack()

            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun showInputDialogAddIngredient() {
        val builder = AlertDialog.Builder(context,R.style.CustomAlertDialogStyle)
        val view = layoutInflater.inflate(R.layout.dialoge_input, null)
        val editText = view.findViewById<EditText>(R.id.dialogeInputET)
        builder.setView(view)
        builder.setPositiveButton(R.string.dialoge_add_button, DialogInterface.OnClickListener { dialog, _ ->
            val inputText = editText.text.toString()
            val addIngredient = Ingredient(name = inputText)
            CoroutineScope(Dispatchers.IO).launch {
                val ingredientId = database.ingredientDao().insert(addIngredient)
                val newIngredient = database.ingredientDao().getIngredientById(ingredientId)
                withContext(Dispatchers.Main){
                    showInputDialog(newIngredient)
                }
            }
            dialog.dismiss()
        })
        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setBottomNavigationVisibility(false)
    }
}