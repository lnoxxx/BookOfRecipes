package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookofrecipes.databinding.FragmentRecipesBinding

class RecipesFragment : Fragment() {

    private lateinit var bindingRecipes: FragmentRecipesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingRecipes = FragmentRecipesBinding.inflate(inflater)
        return bindingRecipes.root
    }

}