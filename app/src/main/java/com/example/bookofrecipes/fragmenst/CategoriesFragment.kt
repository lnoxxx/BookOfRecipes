package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookofrecipes.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment() {

    private lateinit var bindingCategories: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingCategories = FragmentCategoriesBinding.inflate(inflater)
        return bindingCategories.root
    }
}