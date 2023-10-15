package com.example.bookofrecipes.rcAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookofrecipes.R
import com.example.bookofrecipes.dataBase.Ingredient
import com.example.bookofrecipes.databinding.IngredientItemBinding

class IngredientRcAdapter(private val dataSet: Array<Ingredient>):
    RecyclerView.Adapter<IngredientRcAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = IngredientItemBinding.bind(view)
        fun bind(ingredient: Ingredient){
            binding.textView5.text = ingredient.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IngredientRcAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.ingredient_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientRcAdapter.ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}