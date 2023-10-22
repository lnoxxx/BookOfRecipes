package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.databinding.FragmentSearchIngredientBinding
import com.example.bookofrecipes.rcAdapters.SearchIngredientRVAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SearchIngredientFragment : Fragment() {

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
                val adapter = SearchIngredientRVAdapter(ingredientList)
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
                        val adapter = SearchIngredientRVAdapter(ingredientList)
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

}