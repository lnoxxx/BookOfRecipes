package com.example.bookofrecipes.fragmenst

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookofrecipes.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var bindingAdd: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingAdd = FragmentAddBinding.inflate(inflater)
        return bindingAdd.root
    }

}