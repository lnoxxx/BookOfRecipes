package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.FragmentIngredientsBinding
import com.example.bookofrecipes.rcAdapters.IngredientRcAdapter

class IngredientsFragment : Fragment() {

    private lateinit var bindingIngredients: FragmentIngredientsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingIngredients = FragmentIngredientsBinding.inflate(inflater)

        val ingredient1 = Ingredient(name = "Соль")
        val ingredient2 = Ingredient(name = "Сахар")
        val ingredient3 = Ingredient(name = "Лист салата")

        val ingredientList = arrayOf(ingredient1,ingredient2,ingredient3)

        bindingIngredients.rcIngredients.layoutManager = LinearLayoutManager(context)
        val adapter = IngredientRcAdapter(ingredientList)
        bindingIngredients.rcIngredients.adapter = adapter

        return bindingIngredients.root
    }


}