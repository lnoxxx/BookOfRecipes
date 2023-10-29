package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.IngredientCount
import com.example.bookofrecipes.R
import com.example.bookofrecipes.databinding.FragmentChoseIngredientBinding
import com.example.bookofrecipes.rcAdapters.ChoseIngradientRVadapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class ChoseIngredientFragment : Fragment() {

    lateinit var binding: FragmentChoseIngredientBinding

    var ingredientCountList = ArrayList<IngredientCount>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChoseIngredientBinding.inflate(inflater)

        binding.addButton.setOnClickListener {
            it.findNavController().navigate(R.id.searchIngredientFragment)
        }

        setFragmentResultListener("ingSelect"){ key, bundle ->
            val resultId = bundle.getInt("ingId")
            val resultCount = bundle.getString("ingCount")
            val resultName= bundle.getString("ingName")

            val ingredient = IngredientCount(resultId, resultCount, resultName)
            ingredientCountList.add(ingredient)

            val bundleList = Bundle()
            bundleList.putSerializable("ingList", ingredientCountList)
            setFragmentResult("ingResult", bundleList)
        }

        setFragmentResultListener("ingListOpen"){ key, bundle ->
            ingredientCountList = bundle.getSerializable("ingView") as ArrayList<IngredientCount>
            binding.ingredRv.adapter = ChoseIngradientRVadapter(ingredientCountList)
        }

        binding.ingredRv.layoutManager = LinearLayoutManager(context)

        binding.ingredRv.adapter = ChoseIngradientRVadapter(ingredientCountList)


        return binding.root
    }

}