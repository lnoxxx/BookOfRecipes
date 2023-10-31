package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.R
import com.example.bookofrecipes.SharedViewModel
import com.example.bookofrecipes.dataClasses.IngredientCount
import com.example.bookofrecipes.databinding.FragmentChoseIngredientBinding
import com.example.bookofrecipes.rcAdapters.ChoseIngredientRVAdapter

class ChoseIngredientFragment : Fragment() {

    lateinit var binding: FragmentChoseIngredientBinding

    private var ingredientCountList :MutableList<IngredientCount> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChoseIngredientBinding.inflate(inflater)
        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.searchIngredientFragment)
        }

        val viewModel: SharedViewModel by activityViewModels()

        ingredientCountList = viewModel.ingredientCountList.value ?: mutableListOf()

        binding.ingredRv.layoutManager = LinearLayoutManager(context)

        binding.ingredRv.adapter = ChoseIngredientRVAdapter(ingredientCountList)

        return binding.root
    }

}