package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookofrecipes.R
import com.example.bookofrecipes.databinding.FragmentChoseIngredientBinding

class ChoseIngredientFragment : Fragment() {

    lateinit var binding: FragmentChoseIngredientBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentChoseIngredientBinding.inflate(inflater)

        return inflater.inflate(R.layout.fragment_chose_ingredient, container, false)
    }


}