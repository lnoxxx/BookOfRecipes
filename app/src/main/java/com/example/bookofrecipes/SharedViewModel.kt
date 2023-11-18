package com.example.bookofrecipes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookofrecipes.dataClasses.IngredientCount

class SharedViewModel: ViewModel() {
    val ingredientCountList: MutableLiveData<MutableList<IngredientCount>> = MutableLiveData()
    val ingredientEditList: MutableLiveData<MutableList<IngredientCount>> = MutableLiveData()
}