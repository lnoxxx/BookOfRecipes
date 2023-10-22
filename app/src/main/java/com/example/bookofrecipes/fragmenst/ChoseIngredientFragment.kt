package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.bookofrecipes.Application
import com.example.bookofrecipes.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.AppDatabase
import com.example.bookofrecipes.databinding.FragmentChoseIngredientBinding

class ChoseIngredientFragment : Fragment() {

    lateinit var binding: FragmentChoseIngredientBinding

    var ingredientList = ArrayList<IngredientCount>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChoseIngredientBinding.inflate(inflater)

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.searchIngredientFragment)
        }

        return binding.root
    }

}